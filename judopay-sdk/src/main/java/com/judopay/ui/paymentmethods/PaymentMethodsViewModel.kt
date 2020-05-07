package com.judopay.ui.paymentmethods

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.lifecycle.viewModelScope
import com.judopay.Judo
import com.judopay.R
import com.judopay.api.JudoApiService
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.api.model.request.Address
import com.judopay.api.model.request.TokenRequest
import com.judopay.api.model.response.CardDate
import com.judopay.api.model.response.CardToken
import com.judopay.api.model.response.Consumer
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import com.judopay.db.JudoRoomDatabase
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.db.repository.TokenizedCardRepository
import com.judopay.model.Currency
import com.judopay.model.PaymentMethod
import com.judopay.model.PaymentWidgetType
import com.judopay.model.displayName
import com.judopay.model.formatted
import com.judopay.model.paymentButtonType
import com.judopay.model.typeId
import com.judopay.toMap
import com.judopay.ui.common.ButtonState
import com.judopay.ui.paymentmethods.adapter.model.IdealBank
import com.judopay.ui.paymentmethods.adapter.model.IdealBankItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemType
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.ui.paymentmethods.adapter.model.bic
import com.judopay.ui.paymentmethods.components.GooglePayCardViewModel
import com.judopay.ui.paymentmethods.components.NoPaymentMethodSelectedViewModel
import com.judopay.ui.paymentmethods.components.PaymentCallToActionViewModel
import com.judopay.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.ui.paymentmethods.model.CardPaymentMethodModel
import com.judopay.ui.paymentmethods.model.CardViewModel
import com.judopay.ui.paymentmethods.model.Event
import com.judopay.ui.paymentmethods.model.GooglePayPaymentMethodModel
import com.judopay.ui.paymentmethods.model.IdealPaymentCardViewModel
import com.judopay.ui.paymentmethods.model.IdealPaymentMethodModel
import com.judopay.ui.paymentmethods.model.PaymentCardViewModel
import com.judopay.ui.paymentmethods.model.PaymentMethodModel
import java.util.Date
import kotlinx.coroutines.launch

// view-model actions
sealed class PaymentMethodsAction {
    data class DeleteCard(val cardId: Int) : PaymentMethodsAction()
    data class SelectPaymentMethod(val method: PaymentMethod) : PaymentMethodsAction()
    data class SelectStoredCard(val id: Int) : PaymentMethodsAction()
    data class UpdatePayWithGooglePayButtonState(val buttonEnabled: Boolean) :
        PaymentMethodsAction()

    data class EditMode(val isInEditMode: Boolean) : PaymentMethodsAction()
    data class SelectIdealBank(val idealBank: IdealBank) : PaymentMethodsAction()

    object PayWithSelectedStoredCard : PaymentMethodsAction()
    object PayWithSelectedIdealBank : PaymentMethodsAction()
    object Update : PaymentMethodsAction() // TODO: temporary
}

// view-model custom factory to inject the `judo` configuration object
internal class PaymentMethodsViewModelFactory(
    private val cardDate: CardDate,
    private val cardRepository: TokenizedCardRepository,
    private val service: JudoApiService,
    private val application: Application,
    private val judo: Judo
) : NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == PaymentMethodsViewModel::class.java) {
            PaymentMethodsViewModel(cardDate, cardRepository, service, application, judo) as T
        } else super.create(modelClass)
    }
}

class PaymentMethodsViewModel(
    private val cardDate: CardDate,
    private val cardRepository: TokenizedCardRepository,
    private val service: JudoApiService,
    application: Application,
    private val judo: Judo
) : AndroidViewModel(application) {

    val model = MutableLiveData<PaymentMethodsModel>()
    val judoApiCallResult = MutableLiveData<JudoApiCallResult<Receipt>>()
    val payWithIdealObserver = MutableLiveData<Event<String>>()

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
            is PaymentMethodsAction.PayWithSelectedStoredCard -> {
                buildModel(isLoading = true)
                payWithSelectedCard()
            }
            is PaymentMethodsAction.PayWithSelectedIdealBank -> {
                buildModel(isLoading = true)
                payWithSelectedIdealBank()
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
            is PaymentMethodsAction.UpdatePayWithGooglePayButtonState -> buildModel(
                isLoading = !action.buttonEnabled
            )
            is PaymentMethodsAction.EditMode -> buildModel(isInEditMode = action.isInEditMode)
        }
    }

    @Throws(IllegalStateException::class)
    private fun payWithSelectedCard() = viewModelScope.launch {
        model.value?.let { methodModel ->
            if (methodModel.currentPaymentMethod is CardPaymentMethodModel) {
                val card = methodModel.currentPaymentMethod.selectedCard
                card?.let {
                    val entity = cardRepository.findWithId(it.id)
                    if (judo.paymentWidgetType == PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS) {
                        buildReceipt(entity)
                    } else {
                        val request = TokenRequest.Builder()
                            .setAmount(judo.amount.amount)
                            .setCurrency(judo.amount.currency.name)
                            .setJudoId(judo.judoId)
                            .setYourPaymentReference(judo.reference.paymentReference)
                            .setYourConsumerReference(judo.reference.consumerReference)
                            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
                            .setCardLastFour(entity.ending)
                            .setCardToken(entity.token)
                            .setCardType(entity.network.typeId)
                            .setAddress(Address.Builder().build())
                            .build()

                        val response = when (judo.paymentWidgetType) {
                            PaymentWidgetType.PAYMENT_METHODS -> service.tokenPayment(request)
                            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> service.preAuthTokenPayment(
                                request
                            )
                            else -> throw IllegalStateException("Unexpected payment widget type: ${judo.paymentWidgetType}")
                        }
                        cardRepository.updateAllLastUsedToFalse()
                        cardRepository.insert(entity.apply { isLastUsed = true })

                        buildModel()
                        judoApiCallResult.postValue(response)
                    }
                }
            }
        }
    }

    private fun payWithSelectedIdealBank() = viewModelScope.launch {
        model.value?.let { methodModel ->
            if (methodModel.currentPaymentMethod is IdealPaymentMethodModel) {
                payWithIdealObserver.postValue(
                    Event(
                        methodModel.currentPaymentMethod.selectedBank.bic()
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

        if (judo.amount.currency != Currency.EUR) {
            allMethods = judo.paymentMethods.filter { it != PaymentMethod.IDEAL }
        }
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
            shouldDisplayAmount = judo.uiConfiguration.shouldDisplayAmount
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
        cardModel is PaymentCardViewModel && cardDate.apply { cardDate = cardModel.expireDate }.isAfterToday ->
            ButtonState.Enabled(R.string.pay_now)
        else -> ButtonState.Disabled(R.string.pay_now)
    }

    private fun buildReceipt(card: TokenizedCardEntity) = with(card) {
        val receipt = Receipt(
            judoID = judo.judoId.toLong(),
            yourPaymentReference = judo.reference.paymentReference,
            createdAt = Date(),
            amount = judo.amount.amount.toBigDecimal(),
            currency = judo.amount.currency.name,
            consumer = Consumer(yourConsumerReference = judo.reference.consumerReference),
            cardDetails = CardToken(
                lastFour = ending,
                token = token,
                type = network.typeId,
                scheme = network.displayName
            )
        )
        judoApiCallResult.postValue(JudoApiCallResult.Success(receipt))
    }
}
