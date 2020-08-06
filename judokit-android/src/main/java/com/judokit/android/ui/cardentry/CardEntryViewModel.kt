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
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.CardScanningResult
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.model.formatted
import com.judokit.android.model.toInputModel
import com.judokit.android.toMap
import com.judokit.android.ui.cardentry.model.FormFieldType
import com.judokit.android.ui.cardentry.model.FormModel
import com.judokit.android.ui.cardentry.model.InputModel
import com.judokit.android.ui.common.ButtonState
import com.judokit.android.ui.paymentmethods.toTokenizedCardEntity
import kotlinx.coroutines.launch

data class CardEntryFragmentModel(val formModel: FormModel, val displayScanButton: Boolean = true)

sealed class CardEntryAction {
    data class ValidationStatusChanged(val input: InputModel, val isFormValid: Boolean) : CardEntryAction()
    data class InsertCard(val tokenizedCard: CardToken) : CardEntryAction()
    data class ScanCard(val result: CardScanningResult) : CardEntryAction()
    data class EnableFormFields(val formFields: List<FormFieldType>) : CardEntryAction()
    object SubmitForm : CardEntryAction()
}

internal class CardEntryViewModelFactory(
    private val judo: Judo,
    private val service: JudoApiService,
    private val cardRepository: TokenizedCardRepository,
    private val selectedCardNetwork: CardNetwork?,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == CardEntryViewModel::class.java) {
            CardEntryViewModel(
                judo,
                service,
                cardRepository,
                selectedCardNetwork,
                application
            ) as T
        } else super.create(modelClass)
    }
}

class CardEntryViewModel(
    private val judo: Judo,
    private val service: JudoApiService,
    private val cardRepository: TokenizedCardRepository,
    private val selectedCardNetwork: CardNetwork?,
    application: Application
) : AndroidViewModel(application) {

    val model = MutableLiveData<CardEntryFragmentModel>()
    val judoApiCallResult = MutableLiveData<JudoApiCallResult<Receipt>>()
    val securityCodeResult = MutableLiveData<String>()

    private val context = application

    // used when the form needs to be pre populated, ex. `Scan Card`
    private var inputModel = InputModel()

    private var enabledFormFields: List<FormFieldType> = if (selectedCardNetwork != null) {
        mutableListOf(FormFieldType.SECURITY_NUMBER)
    } else {
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
        fields
    }

    val submitButtonText: Int
        get() = when (judo.paymentWidgetType) {
            PaymentWidgetType.REGISTER_CARD -> R.string.register_card
            PaymentWidgetType.CREATE_CARD_TOKEN -> R.string.save_card
            PaymentWidgetType.CHECK_CARD -> R.string.check_card
            PaymentWidgetType.CARD_PAYMENT,
            PaymentWidgetType.PRE_AUTH ->
                if (judo.uiConfiguration.shouldPaymentButtonDisplayAmount) {
                    R.string.pay_amount
                } else {
                    R.string.pay_now
                }
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
            PaymentWidgetType.PAYMENT_METHODS,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS ->
                if (selectedCardNetwork != null) {
                    R.string.pay_now
                } else {
                    R.string.add_card
                }
            else -> R.string.empty
        }

    val amount: String?
        get() = when (judo.paymentWidgetType) {
            PaymentWidgetType.CARD_PAYMENT,
            PaymentWidgetType.PRE_AUTH ->
                if (judo.uiConfiguration.shouldPaymentButtonDisplayAmount)
                    judo.amount.formatted
                else
                    null
            else -> null
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
            is CardEntryAction.ValidationStatusChanged -> {
                inputModel = action.input
                buildModel(isLoading = false, isFormValid = action.isFormValid, cardNetwork = selectedCardNetwork)
            }
            is CardEntryAction.SubmitForm -> {
                buildModel(isLoading = true, isFormValid = true)
                sendRequest()
            }

            is CardEntryAction.ScanCard -> {
                inputModel = action.result.toInputModel()
                buildModel(isLoading = false, isFormValid = false)
            }
            is CardEntryAction.EnableFormFields -> {
                enabledFormFields = action.formFields
                buildModel(isLoading = false, isFormValid = false, cardNetwork = selectedCardNetwork)
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
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS ->
                if (selectedCardNetwork != null) {
                    securityCodeResult.postValue(inputModel.securityNumber)
                    return@launch
                } else {
                    performSaveCardRequest(addressBuilder)
                }
            else -> throw IllegalStateException("Unsupported PaymentWidgetType")
        }

        judoApiCallResult.postValue(result)

        buildModel(isLoading = false, isFormValid = true)
    }

    private suspend fun performCheckCardRequest(addressBuilder: Address.Builder): JudoApiCallResult<Receipt> {
        val request = CheckCardRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setCurrency(judo.amount.currency.name)
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
            .setCurrency(judo.amount.currency.name)
            .setJudoId(judo.judoId)
            .setYourConsumerReference(judo.reference.consumerReference)
            .setYourPaymentMetaData(judo.reference.metaData?.toMap())
            .setAddress(addressBuilder.build())
            .setCardNumber(inputModel.cardNumber)
            .setExpiryDate(inputModel.expirationDate)
            .setCv2(inputModel.securityNumber)
            .setPrimaryAccountDetails(judo.primaryAccountDetails)
            .setAmount(judo.amount.amount)
            .build()

        return service.registerCard(request)
    }

    private suspend fun performPreAuthPaymentRequest(addressBuilder: Address.Builder): JudoApiCallResult<Receipt> {
        val request = PaymentRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(judo.reference.paymentReference)
            .setAmount(judo.amount.amount)
            .setCurrency(judo.amount.currency.name)
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
            .setAmount(judo.amount.amount)
            .setCurrency(judo.amount.currency.name)
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
            .setCurrency(judo.amount.currency.name)
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

    private fun buildModel(isLoading: Boolean, isFormValid: Boolean, cardNetwork: CardNetwork? = null) {
        val buttonState = when {
            isLoading -> ButtonState.Loading
            isFormValid -> ButtonState.Enabled(submitButtonText, amount)
            else -> ButtonState.Disabled(submitButtonText, amount)
        }

        val formModel = FormModel(
            inputModel, // Model to pre fill the form
            enabledFormFields, // Fields enabled
            judo.supportedCardNetworks.toList(), // Supported networks
            buttonState,
            cardNetwork
        )

        val shouldDisplayScanButton = cardNetwork == null
        model.postValue(CardEntryFragmentModel(formModel, shouldDisplayScanButton))
    }

    private fun insert(card: TokenizedCardEntity) = viewModelScope.launch {
        cardRepository.updateAllLastUsedToFalse()
        cardRepository.insert(card)
    }
}
