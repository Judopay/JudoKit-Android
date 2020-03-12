package com.judopay.ui.paymentmethods

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.lifecycle.viewModelScope
import com.judopay.Judo
import com.judopay.R
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.api.model.request.Address
import com.judopay.api.model.request.TokenRequest
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import com.judopay.db.JudoRoomDatabase
import com.judopay.db.repository.TokenizedCardRepository
import com.judopay.model.PaymentMethod
import com.judopay.model.PaymentWidgetType
import com.judopay.model.formatted
import com.judopay.model.typeId
import com.judopay.toMap
import com.judopay.ui.common.ButtonState
import com.judopay.ui.paymentmethods.adapter.model.*
import com.judopay.ui.paymentmethods.components.*
import com.judopay.ui.paymentmethods.model.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// view-model actions
sealed class PaymentMethodsAction {
    data class DeleteCard(val cardId: Int) : PaymentMethodsAction()
    data class SelectPaymentMethod(val method: PaymentMethod) : PaymentMethodsAction()
    data class SelectStoredCard(val id: Int) : PaymentMethodsAction()
    object PayWithSelectedStoredCard : PaymentMethodsAction()
    object Update : PaymentMethodsAction() // TODO: temporary
}

// view-model custom factory to inject the `judo` configuration object
internal class PaymentMethodsViewModelFactory(private val application: Application,
                                              private val judo: Judo) : NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == PaymentMethodsViewModel::class.java) {
            PaymentMethodsViewModel(application, judo) as T
        } else super.create(modelClass)
    }
}

class PaymentMethodsViewModel(application: Application,
                              private val judo: Judo) : AndroidViewModel(application) {

    val model = MutableLiveData<PaymentMethodsModel>()
    val judoApiCallResult = MutableLiveData<JudoApiCallResult<Receipt>>()

    private val context = application
    private val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
    private val cardRepository = TokenizedCardRepository(tokenizedCardDao)
    private val service = JudoApiServiceFactory.createApiService(context, judo)

    val allCardsSync = cardRepository.allCardsSync

    private val selectedPaymentMethod: PaymentMethod
        get() = model.value?.currentPaymentMethod?.type ?: PaymentMethod.CARD

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
            is PaymentMethodsAction.SelectStoredCard -> {
                buildModel(isLoading = false, selectedCardId = action.id)
            }
            is PaymentMethodsAction.Update -> buildModel()
            is PaymentMethodsAction.SelectPaymentMethod -> {
                if (selectedPaymentMethod != action.method) buildModel(action.method, false)
            }
        }
    }

    @Throws(IllegalStateException::class)
    private fun payWithSelectedCard() = viewModelScope.launch {
        model.value?.let { methodModel ->
            if (methodModel.currentPaymentMethod is CardPaymentMethodModel) {
                val card = methodModel.currentPaymentMethod.selectedCard
                card?.let {
                    val entity = cardRepository.findWithId(it.id)

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
                        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> service.preAuthTokenPayment(request)
                        else -> throw IllegalStateException("Unexpected payment widget type: ${judo.paymentWidgetType}")
                    }

                    buildModel()
                    judoApiCallResult.postValue(response)
                }
            }
        }
    }

    private fun deleteCardWithId(id: Int) = viewModelScope.launch {
        cardRepository.deleteCardWithId(id)
    }

    // TODO: too fat, to furious !
    private fun buildModel(selectedMethod: PaymentMethod = selectedPaymentMethod,
                           isLoading: Boolean = false,
                           selectedCardId: Int = selectedCardIdentifier) = viewModelScope.launch {
        val cardModel: CardViewModel

        val recyclerViewData = mutableListOf<PaymentMethodItem>()
        val allMethods = judo.paymentMethods.toList()
        val cards = allCardsSync.value

        if (allMethods.size > 1) {
            recyclerViewData.add(PaymentMethodSelectorItem(PaymentMethodItemType.SELECTOR, allMethods, selectedMethod))
        }

        val method: PaymentMethodModel = when (selectedMethod) {
            PaymentMethod.CARD -> {
                var selectedCard: PaymentMethodSavedCardItem? = null
                if (cards.isNullOrEmpty()) {
                    // placeholder
                    recyclerViewData.add(PaymentMethodGenericItem(PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER))
                    cardModel = NoPaymentMethodSelectedViewModel()
                } else {
                    recyclerViewData.add(PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_HEADER))

                    // cards
                    val cardItems = cards.map { entity ->
                        entity.toPaymentMethodSavedCardItem().apply {
                            isSelected = if (selectedCardId > -1) {
                                id == selectedCardId
                            } else {
                                cards.first() == entity
                            }
                        }
                    }
                    recyclerViewData.addAll(cardItems)

                    // footer
                    recyclerViewData.add(PaymentMethodGenericItem(PaymentMethodItemType.SAVED_CARDS_FOOTER))

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
                cardModel = IdealPaymentCardViewModel()
                IdealPaymentMethodModel(items = recyclerViewData)
            }
        }

        val callToActionModel = PaymentCallToActionViewModel(
                amount = judo.amount.formatted,
                paymentButtonState = buildPaymentButtonState(method.type, isLoading, cardModel)
        )

        val headerViewModel = PaymentMethodsHeaderViewModel(cardModel, callToActionModel)

        model.postValue(PaymentMethodsModel(headerViewModel, method))
    }

    private fun buildPaymentButtonState(method: PaymentMethod,
                                        isLoading: Boolean,
                                        cardModel: CardViewModel): ButtonState {
        if (method == PaymentMethod.CARD) {
            return when {
                isLoading -> ButtonState.Loading
                cardModel is PaymentCardViewModel && SimpleDateFormat("MM/yy", Locale.UK).parse(cardModel.expireDate).after(Date())-> ButtonState.Enabled(R.string.pay_now)
                else -> ButtonState.Disabled(R.string.pay_now)
            }
        }

        return ButtonState.Disabled(R.string.pay_now)
    }

}
