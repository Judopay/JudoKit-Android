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
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.model.TransactionDetail
import com.judopay.judokit.android.model.formatted
import com.judopay.judokit.android.model.paymentButtonType
import com.judopay.judokit.android.service.CardTransactionCallback
import com.judopay.judokit.android.service.CardTransactionService
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.IdealBank
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.IdealBankItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemType
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.judokit.android.ui.paymentmethods.components.GooglePayCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.NoPaymentMethodSelectedViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.PayByBankCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentCallToActionViewModel
import com.judopay.judokit.android.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.CardPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.CardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.Event
import com.judopay.judokit.android.ui.paymentmethods.model.GooglePayPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.IdealPaymentCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.IdealPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.PayByBankPaymentMethodModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentCardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.PaymentMethodModel
import com.zapp.library.merchant.util.PBBAAppUtils
import kotlinx.coroutines.launch

// view-model actions
sealed class PaymentMethodsAction {
    data class DeleteCard(val cardId: Int) : PaymentMethodsAction()
    data class SelectPaymentMethod(val method: PaymentMethod) : PaymentMethodsAction()
    data class SelectStoredCard(val id: Int) : PaymentMethodsAction()
    data class UpdateButtonState(val buttonEnabled: Boolean) :
        PaymentMethodsAction()

    data class EditMode(val isInEditMode: Boolean) : PaymentMethodsAction()
    data class SelectIdealBank(val idealBank: IdealBank) : PaymentMethodsAction()

    object InitiateSelectedCardPayment : PaymentMethodsAction()
    data class PayWithCard(val transactionDetail: TransactionDetail.Builder) : PaymentMethodsAction()
    object PayWithSelectedIdealBank : PaymentMethodsAction()
    object PayWithPayByBank : PaymentMethodsAction()
    object Update : PaymentMethodsAction() // TODO: temporary
}

// view-model custom factory to inject the `judo` configuration object
internal class PaymentMethodsViewModelFactory(
    private val cardDate: CardDate,
    private val cardRepository: TokenizedCardRepository,
    private val cardTransactionService: CardTransactionService,
    private val application: Application,
    private val judo: Judo
) : NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == PaymentMethodsViewModel::class.java) {
            PaymentMethodsViewModel(
                cardDate,
                cardRepository,
                cardTransactionService,
                application,
                judo
            ) as T
        } else super.create(modelClass)
    }
}

class PaymentMethodsViewModel(
    private val cardDate: CardDate,
    private val cardRepository: TokenizedCardRepository,
    private val cardTransactionService: CardTransactionService,
    application: Application,
    private val judo: Judo
) : AndroidViewModel(application) {

    val model = MutableLiveData<PaymentMethodsModel>()
    val judoPaymentResult = MutableLiveData<JudoPaymentResult>()
    val payWithIdealObserver = MutableLiveData<Event<String>>()
    val payWithPayByBankObserver = MutableLiveData<Event<Nothing>>()
    val displayCardEntryObserver = MutableLiveData<Event<CardEntryOptions>>()

    private val context = application

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

    private val selectedBank: IdealBank
        get() {
            model.value?.let { myModel ->
                val method = myModel.currentPaymentMethod
                if (method is IdealPaymentMethodModel) {
                    return method.selectedBank
                }
            }
            return IdealBank.ING_BANK
        }

    init {
        buildModel()
    }

    fun send(action: PaymentMethodsAction) {
        when (action) {
            is PaymentMethodsAction.DeleteCard -> {
                deleteCardWithId(action.cardId)
                buildModel()
            }
            is PaymentMethodsAction.InitiateSelectedCardPayment -> {
                buildModel(isLoading = true)
                val paymentMethod = model.value?.currentPaymentMethod
                payWithSelectedCard(paymentMethod)
            }
            is PaymentMethodsAction.PayWithSelectedIdealBank -> {
                buildModel(isLoading = true)
                payWithSelectedIdealBank()
            }
            is PaymentMethodsAction.PayWithPayByBank -> {
                buildModel(isLoading = true)
                payWithPayByBankObserver.postValue(Event())
            }
            is PaymentMethodsAction.SelectStoredCard -> {
                buildModel(isLoading = false, selectedCardId = action.id)
            }
            is PaymentMethodsAction.SelectIdealBank -> {
                buildModel(isLoading = false, selectedBank = action.idealBank)
            }
            is PaymentMethodsAction.Update -> buildModel()
            is PaymentMethodsAction.SelectPaymentMethod -> {
                if (selectedPaymentMethod != action.method) buildModel(action.method, false)
            }
            is PaymentMethodsAction.UpdateButtonState -> buildModel(
                isLoading = !action.buttonEnabled
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
                judo.uiConfiguration.shouldPaymentMethodsVerifySecurityCode
            val cardNetwork = if (isSecurityCodeRequired) {
                val card = paymentMethod.selectedCard
                card?.network
            } else null
            if (judo.is3DS2Enabled || isSecurityCodeRequired) {
                val cardEntryOptions = CardEntryOptions(
                    fromPaymentMethods = true,
                    shouldDisplayBillingDetails = judo.is3DS2Enabled,
                    shouldDisplaySecurityCode = cardNetwork
                )
                displayCardEntryObserver.postValue(Event(cardEntryOptions))
            } else {
                sendCardPaymentRequest(paymentMethod, TransactionDetail.Builder())
            }
        }
    }

    @Throws(IllegalStateException::class)
    private fun sendCardPaymentRequest(
        paymentMethod: CardPaymentMethodModel,
        transactionDetailBuilder: TransactionDetail.Builder
    ) = viewModelScope.launch {
        val card = paymentMethod.selectedCard
        card?.let {
            val entity = cardRepository.findWithId(it.id)

            val cardTransactionCallback = object : CardTransactionCallback {
                override fun onFinish(result: JudoPaymentResult) {
                    viewModelScope.launch {
                        cardRepository.updateAllLastUsedToFalse()
                        cardRepository.insert(entity.apply { isLastUsed = true })
                        buildModel()
                        judoPaymentResult.postValue(result)
                    }
                }
            }
            transactionDetailBuilder.setCardToken(entity.token)
                .setCardLastFour(entity.ending)
                .setCardType(entity.network)
                .setExpirationDate(entity.expireDate)
            cardTransactionService.tokenPayment(
                transactionDetailBuilder.build(),
                cardTransactionCallback
            )
        }
    }

    private fun payWithSelectedIdealBank() = viewModelScope.launch {
        model.value?.let { methodModel ->
            if (methodModel.currentPaymentMethod is IdealPaymentMethodModel) {
                payWithIdealObserver.postValue(
                    Event(
                        methodModel.currentPaymentMethod.selectedBank.bic
                    )
                )
            }
        }
    }

    private fun deleteCardWithId(id: Int) = viewModelScope.launch {
        cardRepository.deleteCardWithId(id)
    }

    // TODO: needs to be refactored
    private fun buildModel(
        selectedMethod: PaymentMethod = selectedPaymentMethod,
        isLoading: Boolean = false,
        selectedCardId: Int = selectedCardIdentifier,
        isInEditMode: Boolean = false,
        selectedBank: IdealBank = this.selectedBank
    ) = viewModelScope.launch {
        val cardModel: CardViewModel

        val recyclerViewData = mutableListOf<PaymentMethodItem>()
        var allMethods = judo.paymentMethods.toList()
        val cards = allCardsSync.value

        allMethods = filterPaymentMethods(allMethods)

        if (allMethods.size > 1) {
            recyclerViewData.add(
                PaymentMethodSelectorItem(
                    PaymentMethodItemType.SELECTOR,
                    allMethods,
                    selectedMethod
                )
            )
        }

        val method: PaymentMethodModel = when (selectedMethod) {
            PaymentMethod.CARD -> {
                var selectedCard: PaymentMethodSavedCardItem? = null
                if (cards.isNullOrEmpty()) {
                    // placeholder
                    recyclerViewData.add(
                        PaymentMethodGenericItem(
                            PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER,
                            isInEditMode
                        )
                    )
                    cardModel = NoPaymentMethodSelectedViewModel()
                } else {
                    recyclerViewData.add(
                        PaymentMethodGenericItem(
                            PaymentMethodItemType.SAVED_CARDS_HEADER,
                            isInEditMode
                        )
                    )

                    // cards
                    val defaultSelected = cards.map { it.isDefault }.contains(true)
                    val cardItems = cards.map { entity ->
                        entity.toPaymentMethodSavedCardItem().apply {
                            isSelected = when {
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
                            isInEditMode
                        )
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

            PaymentMethod.PAY_BY_BANK -> {
                cardModel = PayByBankCardViewModel()
                PayByBankPaymentMethodModel(items = recyclerViewData)
            }

            PaymentMethod.IDEAL -> {
                val bankItems = IdealBank.values().map {
                    IdealBankItem(idealBank = it).apply { isSelected = it == selectedBank }
                }

                recyclerViewData.addAll(bankItems)

                cardModel = IdealPaymentCardViewModel(idealBank = selectedBank)

                IdealPaymentMethodModel(selectedBank = selectedBank, items = recyclerViewData)
            }
        }

        val callToActionModel = PaymentCallToActionViewModel(
            amount = judo.amount.formatted,
            buttonType = method.type.paymentButtonType,
            paymentButtonState = buildPaymentButtonState(method.type, isLoading, cardModel),
            shouldDisplayAmount = judo.uiConfiguration.shouldPaymentMethodsDisplayAmount
        )

        val headerViewModel = PaymentMethodsHeaderViewModel(cardModel, callToActionModel)
        model.postValue(PaymentMethodsModel(headerViewModel, method))
    }

    private fun buildPaymentButtonState(
        method: PaymentMethod,
        isLoading: Boolean,
        cardModel: CardViewModel
    ): ButtonState = when (method) {
        PaymentMethod.CARD -> payWithCardButtonState(isLoading, cardModel)
        PaymentMethod.PAY_BY_BANK,
        PaymentMethod.GOOGLE_PAY -> if (isLoading) ButtonState.Disabled(R.string.empty) else ButtonState.Enabled(
            R.string.empty
        )
        PaymentMethod.IDEAL ->
            if (isLoading) ButtonState.Loading else ButtonState.Enabled(R.string.pay_now)
    }

    private fun payWithCardButtonState(
        isLoading: Boolean,
        cardModel: CardViewModel
    ): ButtonState = when {
        isLoading -> ButtonState.Loading
        cardModel is PaymentCardViewModel && cardDate.apply {
            cardDate = cardModel.expireDate
        }.isAfterToday ->
            ButtonState.Enabled(R.string.pay_now)
        else -> ButtonState.Disabled(R.string.pay_now)
    }

    private fun filterPaymentMethods(allMethods: List<PaymentMethod>): List<PaymentMethod> {
        var paymentMethods = allMethods
        if (judo.amount.currency != Currency.EUR) {
            paymentMethods = judo.paymentMethods.filter { it != PaymentMethod.IDEAL }
        }
        if (judo.amount.currency != Currency.GBP || !PBBAAppUtils.isCFIAppAvailable(context)) {
            paymentMethods = paymentMethods.filter { it != PaymentMethod.PAY_BY_BANK }
        }
        return paymentMethods
    }
}
