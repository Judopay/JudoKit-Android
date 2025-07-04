package com.judopay.judokit.android.ui.paymentmethods

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.lifecycle.viewModelScope
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.response.CardDate
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.formatted
import com.judopay.judokit.android.model.paymentButtonType
import com.judopay.judokit.android.service.CardTransactionManager
import com.judopay.judokit.android.service.CardTransactionManagerResultListener
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemType
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.judokit.android.ui.paymentmethods.components.GooglePayCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.NoPaymentMethodSelectedViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentCallToActionViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.CardPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.CardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.Event
import com.judopay.judokit.android.ui.paymentmethods.model.GooglePayPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentMethodModel
import kotlinx.coroutines.launch

// view-model actions
sealed class PaymentMethodsAction {
    data class DeleteCard(
        val cardId: Int,
    ) : PaymentMethodsAction()

    data class SelectPaymentMethod(
        val method: PaymentMethod,
    ) : PaymentMethodsAction()

    data class SelectStoredCard(
        val id: Int,
    ) : PaymentMethodsAction()

    data class UpdateButtonState(
        val buttonEnabled: Boolean,
    ) : PaymentMethodsAction()

    data class EditMode(
        val isInEditMode: Boolean,
    ) : PaymentMethodsAction()

    object InitiateSelectedCardPayment : PaymentMethodsAction()

    data class PayWithCard(
        val transactionDetail: TransactionDetails.Builder,
    ) : PaymentMethodsAction()

    object Update : PaymentMethodsAction()

    object SubscribeToCardTransactionManagerResults : PaymentMethodsAction()

    object UnSubscribeToCardTransactionManagerResults : PaymentMethodsAction()
}

// view-model custom factory to inject the `judo` configuration object
internal class PaymentMethodsViewModelFactory(
    private val cardDate: CardDate,
    private val cardRepository: TokenizedCardRepository,
    private val cardTransactionManager: CardTransactionManager,
    private val application: Application,
    private val judo: Judo,
) : NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass == PaymentMethodsViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            PaymentMethodsViewModel(
                cardDate,
                cardRepository,
                cardTransactionManager,
                application,
                judo,
            ) as T
        } else {
            super.create(modelClass)
        }
}

class PaymentMethodsViewModel(
    private val cardDate: CardDate,
    private val cardRepository: TokenizedCardRepository,
    private val cardTransactionManager: CardTransactionManager,
    application: Application,
    private val judo: Judo,
) : AndroidViewModel(application),
    CardTransactionManagerResultListener {
    val model = MutableLiveData<PaymentMethodsModel>()
    val judoPaymentResult = MutableLiveData<JudoPaymentResult>()
    val displayCardEntryObserver = MutableLiveData<Event<CardEntryOptions>>()

    val allCardsSync = cardRepository.allCardsSync

    private val selectedPaymentMethod: PaymentMethod
        get() = model.value?.currentPaymentMethod?.type ?: judo.paymentMethods.first()

    private val selectedCardIdentifier: Int
        get() {
            model.value?.let { myModel ->
                val method = myModel.currentPaymentMethod
                if (method is CardPaymentMethodModel) {
                    return method.selectedCard?.id ?: -1
                }
            }
            return -1
        }

    init {
        buildModel()
    }

    @Suppress("CyclomaticComplexMethod")
    fun send(action: PaymentMethodsAction) {
        when (action) {
            is PaymentMethodsAction.SubscribeToCardTransactionManagerResults -> {
                cardTransactionManager.registerResultListener(this)
            }
            is PaymentMethodsAction.UnSubscribeToCardTransactionManagerResults -> {
                cardTransactionManager.unRegisterResultListener(this)
            }
            is PaymentMethodsAction.DeleteCard -> {
                deleteCardWithId(action.cardId)
                buildModel()
            }
            is PaymentMethodsAction.InitiateSelectedCardPayment -> {
                buildModel(isLoading = true)
                val paymentMethod = model.value?.currentPaymentMethod
                payWithSelectedCard(paymentMethod)
            }
            is PaymentMethodsAction.SelectStoredCard -> {
                buildModel(isLoading = false, selectedCardId = action.id)
            }
            is PaymentMethodsAction.Update ->
                buildModel(
                    isLoading =
                        model.value
                            ?.headerModel
                            ?.callToActionModel
                            ?.paymentButtonState == ButtonState.Loading,
                )
            is PaymentMethodsAction.SelectPaymentMethod -> {
                if (selectedPaymentMethod != action.method) buildModel(action.method, false)
            }
            is PaymentMethodsAction.UpdateButtonState ->
                buildModel(
                    isLoading = !action.buttonEnabled,
                )
            is PaymentMethodsAction.EditMode -> buildModel(isInEditMode = action.isInEditMode)
            is PaymentMethodsAction.PayWithCard -> {
                buildModel(isLoading = true)
                val paymentMethod = model.value?.currentPaymentMethod
                if (paymentMethod is CardPaymentMethodModel) {
                    sendCardPaymentRequest(paymentMethod, action.transactionDetail)
                }
            }
        }
    }

    private fun payWithSelectedCard(paymentMethod: PaymentMethodModel?) {
        if (paymentMethod is CardPaymentMethodModel) {
            val isSecurityCodeRequired =
                judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode || judo.uiConfiguration.shouldAskForCSC
            val cardNetwork =
                if (isSecurityCodeRequired) {
                    val card = paymentMethod.selectedCard
                    card?.network
                } else {
                    null
                }
            if (judo.uiConfiguration.shouldAskForBillingInformation ||
                isSecurityCodeRequired ||
                judo.uiConfiguration.shouldAskForCardholderName
            ) {
                val cardEntryOptions =
                    CardEntryOptions(
                        isPresentedFromPaymentMethods = true,
                        cardNetwork = cardNetwork,
                    )
                displayCardEntryObserver.postValue(Event(cardEntryOptions))
            } else {
                sendCardPaymentRequest(paymentMethod, TransactionDetails.Builder())
            }
        }
    }

    @Throws(IllegalStateException::class)
    private fun sendCardPaymentRequest(
        paymentMethod: CardPaymentMethodModel,
        transactionDetailBuilder: TransactionDetails.Builder,
    ) = viewModelScope.launch {
        val card = paymentMethod.selectedCard
        card?.let {
            val entity = cardRepository.findWithId(it.id)

            viewModelScope.launch {
                cardRepository.updateAllLastUsedToFalse()
                cardRepository.insert(entity.apply { isLastUsed = true })
            }

            transactionDetailBuilder
                .setCardToken(entity.token)
                .setCardLastFour(entity.ending)
                .setCardType(entity.network)
                .setExpirationDate(entity.expireDate)
            if (!judo.uiConfiguration.shouldAskForCardholderName) {
                transactionDetailBuilder.setCardHolderName(entity.cardholderName)
            }

            if (judo.paymentWidgetType == PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS) {
                cardTransactionManager.preAuthWithToken(
                    transactionDetailBuilder.build(),
                    PaymentMethodsViewModel::class.java.name,
                )
            } else {
                cardTransactionManager.paymentWithToken(
                    transactionDetailBuilder.build(),
                    PaymentMethodsViewModel::class.java.name,
                )
            }
        }
    }

    // CardTransactionManagerResultListener
    override fun onCardTransactionResult(result: JudoPaymentResult) {
        buildModel()
        judoPaymentResult.postValue(result)
    }

    private fun deleteCardWithId(id: Int) =
        viewModelScope.launch {
            cardRepository.deleteCardWithId(id)
        }

    @Suppress("LongMethod", "CyclomaticComplexMethod")
    private fun buildModel(
        selectedMethod: PaymentMethod = selectedPaymentMethod,
        isLoading: Boolean = false,
        selectedCardId: Int = selectedCardIdentifier,
        isInEditMode: Boolean = false,
    ) = viewModelScope.launch {
        val cardModel: CardViewModel

        val recyclerViewData = mutableListOf<PaymentMethodItem>()
        var allMethods = judo.paymentMethods.toList()
        val cards = allCardsSync.value

        if (allMethods.size > 1) {
            recyclerViewData.add(
                PaymentMethodSelectorItem(
                    PaymentMethodItemType.SELECTOR,
                    allMethods,
                    selectedMethod,
                ),
            )
        }

        val method: PaymentMethodModel =
            when (selectedMethod) {
                PaymentMethod.CARD -> {
                    var selectedCard: PaymentMethodSavedCardItem? = null
                    if (cards.isNullOrEmpty()) {
                        // placeholder
                        recyclerViewData.add(
                            PaymentMethodGenericItem(
                                PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER,
                                isInEditMode,
                            ),
                        )
                        cardModel = NoPaymentMethodSelectedViewModel()
                    } else {
                        recyclerViewData.add(
                            PaymentMethodGenericItem(
                                PaymentMethodItemType.SAVED_CARDS_HEADER,
                                isInEditMode,
                            ),
                        )

                        // cards
                        val defaultSelected = cards.map { it.isDefault }.contains(true)
                        val cardItems =
                            cards.map { entity ->
                                entity.toPaymentMethodSavedCardItem().apply {
                                    isSelected =
                                        when {
                                            selectedCardId > -1 -> id == selectedCardId
                                            defaultSelected -> entity.isDefault
                                            else -> entity.isLastUsed
                                        }
                                    this.isInEditMode = isInEditMode
                                }
                            }
                        recyclerViewData.addAll(cardItems)

                        // footer
                        recyclerViewData.add(
                            PaymentMethodGenericItem(
                                PaymentMethodItemType.SAVED_CARDS_FOOTER,
                                isInEditMode,
                            ),
                        )

                        selectedCard = cardItems.firstOrNull { it.isSelected } ?: cardItems.first()
                        cardModel = selectedCard.toPaymentCardViewModel()
                    }
                    CardPaymentMethodModel(selectedCard = selectedCard, items = recyclerViewData)
                }

                PaymentMethod.GOOGLE_PAY -> {
                    cardModel = GooglePayCardViewModel()
                    GooglePayPaymentMethodModel(items = recyclerViewData)
                }
            }

        val callToActionModel =
            PaymentCallToActionViewModel(
                amount = judo.amount.formatted,
                buttonType = method.type.paymentButtonType,
                paymentButtonState = buildPaymentButtonState(method.type, isLoading, cardModel),
                shouldDisplayAmount = judo.uiConfiguration.shouldPaymentMethodsDisplayAmount,
            )

        val headerViewModel = PaymentMethodsHeaderViewModel(cardModel, callToActionModel)
        model.postValue(PaymentMethodsModel(headerViewModel, method))
    }

    private fun buildPaymentButtonState(
        method: PaymentMethod,
        isLoading: Boolean,
        cardModel: CardViewModel,
    ): ButtonState =
        when (method) {
            PaymentMethod.CARD -> payWithCardButtonState(isLoading, cardModel)
            PaymentMethod.GOOGLE_PAY ->
                if (isLoading) {
                    ButtonState.Disabled(R.string.jp_empty)
                } else {
                    ButtonState.Enabled(R.string.jp_empty)
                }
        }

    private fun payWithCardButtonState(
        isLoading: Boolean,
        cardModel: CardViewModel,
    ): ButtonState =
        when {
            isLoading -> ButtonState.Loading
            cardModel is PaymentCardViewModel &&
                cardDate
                    .apply {
                        date = cardModel.expireDate
                    }.isAfterToday
            ->
                ButtonState.Enabled(R.string.jp_pay_now)
            else -> ButtonState.Disabled(R.string.jp_pay_now)
        }
}
