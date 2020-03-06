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
import com.judopay.api.model.response.CardToken
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import com.judopay.db.JudoRoomDatabase
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.db.repository.TokenizedCardRepository
import com.judopay.model.PaymentMethod
import com.judopay.model.formatted
import com.judopay.toMap
import com.judopay.ui.common.ButtonState
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemType
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import com.judopay.ui.paymentmethods.components.GooglePayCardViewModel
import com.judopay.ui.paymentmethods.components.IdealPaymentCardViewModel
import com.judopay.ui.paymentmethods.components.NoPaymentMethodSelectedViewModel
import com.judopay.ui.paymentmethods.components.PaymentCallToActionViewModel
import com.judopay.ui.paymentmethods.components.PaymentCardViewModel
import com.judopay.ui.paymentmethods.components.PaymentMethodsHeaderViewModel
import com.judopay.ui.paymentmethods.model.CardPaymentMethodModel
import com.judopay.ui.paymentmethods.model.CardViewModel
import com.judopay.ui.paymentmethods.model.GooglePayPaymentMethodModel
import com.judopay.ui.paymentmethods.model.IdealPaymentMethodModel
import com.judopay.ui.paymentmethods.model.PaymentMethodModel
import kotlinx.coroutines.launch

sealed class PaymentMethodsAction {
    data class InsertCard(val tokenizedCard: CardToken) : PaymentMethodsAction()
    data class DeleteCard(val cardId: Int) : PaymentMethodsAction()
    data class SelectPaymentMethod(val method: PaymentMethod) : PaymentMethodsAction()
    data class SelectStoredCard(val id: Int) : PaymentMethodsAction()
    object PayWithSelectedStoredCard : PaymentMethodsAction()
}

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
    val receipt = MutableLiveData<Receipt>()

    private val context = application
    private val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
    private val cardRepository = TokenizedCardRepository(tokenizedCardDao)
    private val service = JudoApiServiceFactory.createApiService(context, judo)

    init {
        buildModel(PaymentMethod.CARD, false)
    }

    fun send(action: PaymentMethodsAction) {
        val paymentMethod = model.value?.currentPaymentMethod?.type ?: PaymentMethod.CARD

        when (action) {
            is PaymentMethodsAction.InsertCard -> {
                val entity = action.tokenizedCard.toTokenizedCardEntity(context)
                insert(entity)
                buildModel(paymentMethod, false) // TODO: temporary
            }
            is PaymentMethodsAction.DeleteCard -> {
                deleteCardWithId(action.cardId)
                buildModel(paymentMethod, false) // TODO: temporary
            }
            is PaymentMethodsAction.PayWithSelectedStoredCard -> {
                buildModel(paymentMethod, true)
                payWithSelectedCard()
            }

            is PaymentMethodsAction.SelectStoredCard -> {
                buildModel(paymentMethod, false, action.id)
            }

            is PaymentMethodsAction.SelectPaymentMethod -> {
                if (paymentMethod != action.method) buildModel(action.method, false)
            }
        }
    }

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
                            .setCardType(1)
                            .setAddress(Address.Builder().build())
                            .build()

                    when (val response = service.tokenPayment(request)) {
                        is JudoApiCallResult.Success -> receipt.postValue(response.data)
                    }
                    // TODO: temporary
                    val paymentMethod = model.value?.currentPaymentMethod?.type
                            ?: PaymentMethod.CARD
                    buildModel(paymentMethod, false)
                }
            }
        }
    }

    private fun insert(card: TokenizedCardEntity) = viewModelScope.launch {
        cardRepository.insert(card)
    }

    private fun deleteCardWithId(id: Int) = viewModelScope.launch {
        cardRepository.deleteCardWithId(id)
    }

    private fun buildModel(selectedMethod: PaymentMethod,
                           isLoading: Boolean,
                           selectedCardId: Int = previouslySelectedCardId()) = viewModelScope.launch {
        val cardModel: CardViewModel

        val recyclerViewData = mutableListOf<PaymentMethodItem>()
        val allMethods = judo.paymentMethods.toList()
        val cards = cardRepository.findAllCards()

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

                    selectedCard = cardItems.first { it.isSelected }
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

        val isOptionSelected = cardModel is PaymentCardViewModel

        val callToActionModel = PaymentCallToActionViewModel(
                amount = judo.amount.formatted,
                paymentButtonState = paymentButtonState(method.type, isLoading, isOptionSelected)
        )

        val headerViewModel = PaymentMethodsHeaderViewModel(cardModel, callToActionModel)

        model.postValue(PaymentMethodsModel(headerViewModel, method))
    }

    private fun paymentButtonState(method: PaymentMethod,
                                   isLoading: Boolean,
                                   isOptionSelected: Boolean): ButtonState {
        val text = R.string.pay_now

        if (method == PaymentMethod.CARD) {
            return when {
                isLoading -> ButtonState.Loading
                isOptionSelected -> ButtonState.Enabled(text)
                else -> ButtonState.Disabled(text)
            }
        }

        return ButtonState.Disabled(text)
    }

    private fun previouslySelectedCardId(): Int {
        model.value?.let { myModel ->
            val method = myModel.currentPaymentMethod
            if (method is CardPaymentMethodModel) {
                return method.selectedCard?.id ?: -1
            }
        }
        return -1
    }

}
