package com.judokit.android.ui.cardentry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judokit.android.Judo
import com.judokit.android.R
import com.judokit.android.api.JudoApiService
import com.judokit.android.api.model.request.Address
import com.judokit.android.api.model.request.CheckCardRequest
import com.judokit.android.api.model.request.PaymentRequest
import com.judokit.android.api.model.request.RegisterCardRequest
import com.judokit.android.api.model.request.SaveCardRequest
import com.judokit.android.api.model.response.CardToken
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.Receipt
import com.judokit.android.db.entity.TokenizedCardEntity
import com.judokit.android.db.repository.TokenizedCardRepository
import com.judokit.android.model.CardScanningResult
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.model.toInputModel
import com.judokit.android.toMap
import com.judokit.android.ui.cardentry.components.FormFieldType
import com.judokit.android.ui.cardentry.components.FormModel
import com.judokit.android.ui.cardentry.components.InputModel
import com.judokit.android.ui.common.ButtonState
import com.judokit.android.ui.paymentmethods.toTokenizedCardEntity
import kotlinx.coroutines.launch

data class CardEntryFragmentModel(val formModel: FormModel)

sealed class CardEntryAction {
    data class ValidationPassed(val input: InputModel) : CardEntryAction()
    data class InsertCard(val tokenizedCard: CardToken) : CardEntryAction()
    data class ScanCard(val result: CardScanningResult) : CardEntryAction()
    object SubmitForm : CardEntryAction()
}

internal class CardEntryViewModelFactory(
    private val judo: Judo,
    private val service: JudoApiService,
    private val cardRepository: TokenizedCardRepository,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == CardEntryViewModel::class.java) {
            CardEntryViewModel(judo, service, cardRepository, application) as T
        } else super.create(modelClass)
    }
}

class CardEntryViewModel(
    private val judo: Judo,
    private val service: JudoApiService,
    private val cardRepository: TokenizedCardRepository,
    application: Application
) : AndroidViewModel(application) {

    val model = MutableLiveData<CardEntryFragmentModel>()
    val judoApiCallResult = MutableLiveData<JudoApiCallResult<Receipt>>()

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

    val submitButtonText: Int
        get() = when (judo.paymentWidgetType) {
            PaymentWidgetType.CARD_PAYMENT,
            PaymentWidgetType.PRE_AUTH,
            PaymentWidgetType.REGISTER_CARD,
            PaymentWidgetType.CREATE_CARD_TOKEN,
            PaymentWidgetType.CHECK_CARD -> R.string.pay_now
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
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

            is CardEntryAction.ScanCard -> {
                inputModel = action.result.toInputModel()
                buildModel(isLoading = false, isFormValid = false)
            }
        }
    }

    private fun sendRequest() = viewModelScope.launch {
        val addressBuilder = Address.Builder()

        if (judo.uiConfiguration.avsEnabled) {
            addressBuilder
                .setBillingCountry(inputModel.country)
                .setPostCode(inputModel.postCode)
        }

        val result = when (judo.paymentWidgetType) {
            PaymentWidgetType.CARD_PAYMENT -> performPaymentRequest(addressBuilder)
            PaymentWidgetType.PRE_AUTH -> performPreAuthPaymentRequest(addressBuilder)
            PaymentWidgetType.REGISTER_CARD -> performRegisterCardRequest(addressBuilder)
            PaymentWidgetType.CHECK_CARD -> performCheckCardRequest(addressBuilder)
            PaymentWidgetType.CREATE_CARD_TOKEN,
            PaymentWidgetType.PAYMENT_METHODS,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS -> performSaveCardRequest(
                addressBuilder
            )
            else -> throw IllegalStateException("Unsupported PaymentWidgetType")
        }

        judoApiCallResult.postValue(result)

        buildModel(isLoading = false, isFormValid = true)
    }

    private suspend fun performCheckCardRequest(addressBuilder: Address.Builder): JudoApiCallResult<Receipt> {
        val request = CheckCardRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setCurrency(judo.amount?.currency?.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
            .setAddress(addressBuilder.build())
            .setCardNumber(inputModel.cardNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setCv2(inputModel.securityNumber)
            .setPrimaryAccountDetails(judo.primaryAccountDetails)
            .build()

        return service.checkCard(request)
    }

    private suspend fun performRegisterCardRequest(addressBuilder: Address.Builder): JudoApiCallResult<Receipt> {
        val request = RegisterCardRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setCurrency(judo.amount?.currency?.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
            .setAddress(addressBuilder.build())
            .setCardNumber(inputModel.cardNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setCv2(inputModel.securityNumber)
            .setPrimaryAccountDetails(judo.primaryAccountDetails)
            .setAmount(judo.amount?.amount)
            .build()

        return service.registerCard(request)
    }

    private suspend fun performPreAuthPaymentRequest(addressBuilder: Address.Builder): JudoApiCallResult<Receipt> {
        val request = PaymentRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setAmount(judo.amount?.amount)
            .setCurrency(judo.amount?.currency?.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
            .setAddress(addressBuilder.build())
            .setCardNumber(inputModel.cardNumber)
            .setCv2(inputModel.securityNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setPrimaryAccountDetails(judo.primaryAccountDetails)
            .build()

        return service.preAuthPayment(request)
    }

    private suspend fun performPaymentRequest(addressBuilder: Address.Builder): JudoApiCallResult<Receipt> {
        val request = PaymentRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setAmount(judo.amount?.amount)
            .setCurrency(judo.amount?.currency?.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
            .setAddress(addressBuilder.build())
            .setCardNumber(inputModel.cardNumber)
            .setCv2(inputModel.securityNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setPrimaryAccountDetails(judo.primaryAccountDetails)
            .build()

        return service.payment(request)
    }

    private suspend fun performSaveCardRequest(addressBuilder: Address.Builder): JudoApiCallResult<Receipt> {
        val request = SaveCardRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setCurrency(judo.amount?.currency?.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
            .setAddress(addressBuilder.build())
            .setCardNumber(inputModel.cardNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setCv2(inputModel.securityNumber)
            .setPrimaryAccountDetails(judo.primaryAccountDetails)
            .build()

        return service.saveCard(request)
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
