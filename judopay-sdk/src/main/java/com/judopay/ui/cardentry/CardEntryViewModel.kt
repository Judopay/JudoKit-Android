package com.judopay.ui.cardentry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judopay.Judo
import com.judopay.R
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.api.model.request.Address
import com.judopay.api.model.request.SaveCardRequest
import com.judopay.api.model.response.CardToken
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import com.judopay.db.JudoRoomDatabase
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.db.repository.TokenizedCardRepository
import com.judopay.model.PaymentWidgetType
import com.judopay.ui.cardentry.components.FormFieldType
import com.judopay.ui.cardentry.components.FormModel
import com.judopay.ui.cardentry.components.InputModel
import com.judopay.ui.common.ButtonState
import com.judopay.ui.paymentmethods.toTokenizedCardEntity
import kotlinx.coroutines.launch

data class CardEntryFragmentModel(val formModel: FormModel)

sealed class CardEntryAction {
    data class ValidationPassed(val input: InputModel) : CardEntryAction()
    data class InsertCard(val tokenizedCard: CardToken) : CardEntryAction()
    object SubmitForm : CardEntryAction()
}

internal class CardEntryViewModelFactory(
    private val application: Application,
    private val judo: Judo
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == CardEntryViewModel::class.java) {
            CardEntryViewModel(application, judo) as T
        } else super.create(modelClass)
    }
}

class CardEntryViewModel(
    application: Application,
    private val judo: Judo
) : AndroidViewModel(application) {

    val model = MutableLiveData<CardEntryFragmentModel>()
    val judoApiCallResult = MutableLiveData<JudoApiCallResult<Receipt>>()

    private val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
    private val cardRepository = TokenizedCardRepository(tokenizedCardDao)

    private val context = application

    // used when the form needs to be pre populated, ex. `Scan Card`
    private var inputModel = InputModel()

    private val enabledFormFields: List<FormFieldType>
        get() {
            val fields = mutableListOf(
                FormFieldType.NUMBER,
                FormFieldType.HOLDER_NAME,
                FormFieldType.EXPIRATION_DATE,
                FormFieldType.SECURITY_NUMBER
            )

            if (judo.uiConfiguration.avsEnabled) {
                fields.add(FormFieldType.COUNTRY)
                fields.add(FormFieldType.POST_CODE)
            }

            return fields
        }

    private val submitButtonText: Int
        get() = when (judo.paymentWidgetType) {
            PaymentWidgetType.CARD_PAYMENT,
            PaymentWidgetType.PRE_AUTH_CARD_PAYMENT,
            PaymentWidgetType.CREATE_CARD_TOKEN,
            PaymentWidgetType.SAVE_CARD,
            PaymentWidgetType.CHECK_CARD -> R.string.pay_now
            PaymentWidgetType.PAYMENT_METHODS,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> R.string.add_card
            else -> R.string.empty
        }

    init {
        buildModel(isLoading = false, isFormValid = false)
    }

    fun send(action: CardEntryAction) {
        when (action) {
            is CardEntryAction.InsertCard -> {
                val entity = action.tokenizedCard.toTokenizedCardEntity(context)
                insert(entity)
            }
            is CardEntryAction.ValidationPassed -> {
                inputModel = action.input
                buildModel(isLoading = false, isFormValid = true)
            }
            is CardEntryAction.SubmitForm -> {
                buildModel(isLoading = true, isFormValid = true)
                sendRequest()
            }
        }
    }

    private fun sendRequest() = viewModelScope.launch {
        val service = JudoApiServiceFactory.createApiService(context, judo)
        val addressBuilder = Address.Builder()

        if (judo.uiConfiguration.avsEnabled) {
            addressBuilder
                .setBillingCountry(inputModel.country)
                .setPostCode(inputModel.postCode)
        }

        // TODO: this to be dynamically built
        val request = SaveCardRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setAmount(judo.amount.amount)
            .setCurrency(judo.amount.currency.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(emptyMap())
            .setAddress(addressBuilder.build())
            .setCardNumber(inputModel.cardNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setCv2(inputModel.securityNumber)
            .build()

        val result = service.saveCard(request)
        judoApiCallResult.postValue(result)

        buildModel(isLoading = false, isFormValid = true)
    }

    private fun buildModel(isLoading: Boolean, isFormValid: Boolean) {
        val buttonState = when {
            isLoading -> ButtonState.Loading
            isFormValid -> ButtonState.Enabled(submitButtonText)
            else -> ButtonState.Disabled(submitButtonText)
        }

        val formModel = FormModel(
            inputModel, // Model to pre fill the form
            enabledFormFields, // Fields enabled
            judo.supportedCardNetworks.toList(), // Supported networks
            buttonState
        )

        model.postValue(CardEntryFragmentModel(formModel))
    }

    private fun insert(card: TokenizedCardEntity) = viewModelScope.launch {
        cardRepository.updateAllLastUsedToFalse()
        cardRepository.insert(card)
    }
}
