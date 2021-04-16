package com.judopay.judokit.android.ui.cardentry

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.CardScanningResult
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.formatted
import com.judopay.judokit.android.model.toInputModel
import com.judopay.judokit.android.service.CardTransactionCallback
import com.judopay.judokit.android.service.CardTransactionService
import com.judopay.judokit.android.ui.cardentry.model.FormFieldType
import com.judopay.judokit.android.ui.cardentry.model.FormModel
import com.judopay.judokit.android.ui.cardentry.model.InputModel
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.common.isDependencyPresent
import com.judopay.judokit.android.ui.paymentmethods.toTokenizedCardEntity
import kotlinx.coroutines.launch

private const val PAY_CARDS_DEPENDENCY = "cards.pay.paycardsrecognizer.sdk.ScanCardIntent"

sealed class CardEntryNavigation {
    object Card: CardEntryNavigation()
    object Billing: CardEntryNavigation()
}

data class CardEntryFragmentModel(val formModel: FormModel, val displayScanButton: Boolean = true, val isOnCardEntryForm: Boolean = true)

sealed class CardEntryAction {
    data class ValidationStatusChanged(val input: InputModel, val isFormValid: Boolean) :
        CardEntryAction()

    data class InsertCard(val tokenizedCard: CardToken) : CardEntryAction()
    data class ScanCard(val result: CardScanningResult) : CardEntryAction()
    data class EnableFormFields(val formFields: List<FormFieldType>) : CardEntryAction()
    object SubmitCardEntryForm : CardEntryAction()
    object SubmitBillingDetailsForm : CardEntryAction()
    object PressBackButton : CardEntryAction()
}

internal class CardEntryViewModelFactory(
    private val judo: Judo,
    private val cardTransactionService: CardTransactionService,
    private val cardRepository: TokenizedCardRepository,
    private val selectedCardNetwork: CardNetwork?,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == CardEntryViewModel::class.java) {
            CardEntryViewModel(
                judo,
                cardTransactionService,
                cardRepository,
                selectedCardNetwork,
                application
            ) as T
        } else super.create(modelClass)
    }
}

class CardEntryViewModel(
    private val judo: Judo,
    private val cardTransactionService: CardTransactionService,
    private val cardRepository: TokenizedCardRepository,
    private val selectedCardNetwork: CardNetwork?,
    application: Application
) : AndroidViewModel(application) {

    val model = MutableLiveData<CardEntryFragmentModel>()
    val judoPaymentResult = MutableLiveData<JudoPaymentResult>()
    val securityCodeResult = MutableLiveData<String>()
    val navigationObserver = MutableLiveData<CardEntryNavigation>()

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

    private val continueButtonText: Int
        get() = when (judo.paymentWidgetType) {
            PaymentWidgetType.CREATE_CARD_TOKEN -> R.string.save_card
            PaymentWidgetType.REGISTER_CARD,
            PaymentWidgetType.CHECK_CARD,
            PaymentWidgetType.CARD_PAYMENT,
            PaymentWidgetType.PRE_AUTH -> R.string.continue_text
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
            PaymentWidgetType.PAYMENT_METHODS,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS ->
                if (selectedCardNetwork != null) {
                    R.string.continue_text
                } else {
                    R.string.save_card
                }
            else -> R.string.empty
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
                    R.string.save_card
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
                buildModel(
                    isLoading = false,
                    isFormValid = action.isFormValid,
                    cardNetwork = selectedCardNetwork
                )
            }
            is CardEntryAction.SubmitCardEntryForm -> {
                if (judo.is3DS2Enabled) {
                    buildModel(isLoading = false, isFormValid = true, isOnCardEntryForm = false)
                    navigationObserver.postValue(CardEntryNavigation.Billing)
                } else {
                    buildModel(isLoading = true, isFormValid = true)
                    sendRequest()
                }
            }
            is CardEntryAction.SubmitBillingDetailsForm -> {
                buildModel(isLoading = true, isFormValid = true, isOnCardEntryForm = false)
                sendRequest()
            }

            is CardEntryAction.ScanCard -> {
                inputModel = action.result.toInputModel()
                buildModel(isLoading = false, isFormValid = false)
            }
            is CardEntryAction.EnableFormFields -> {
                enabledFormFields = action.formFields
                buildModel(
                    isLoading = false,
                    isFormValid = false,
                    cardNetwork = selectedCardNetwork
                )
            }
            CardEntryAction.PressBackButton -> {
                buildModel(isLoading = false, isFormValid = true)
                navigationObserver.postValue(CardEntryNavigation.Card)
            }
        }
    }

    private fun sendRequest() = viewModelScope.launch {
        if (judo.paymentWidgetType == PaymentWidgetType.CREATE_CARD_TOKEN ||
            judo.paymentWidgetType == PaymentWidgetType.PAYMENT_METHODS ||
            judo.paymentWidgetType == PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS ||
            judo.paymentWidgetType == PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS
        ) {
            if (selectedCardNetwork != null) {
                securityCodeResult.postValue(inputModel.securityNumber)
                return@launch
            }
        }

        val cardTransactionCallback = object : CardTransactionCallback {
            override fun onFinish(result: JudoPaymentResult) {
                judoPaymentResult.postValue(result)
            }
        }

        cardTransactionService.makeTransaction(inputModel, cardTransactionCallback)

        buildModel(isLoading = false, isFormValid = true)
    }

    private fun buildModel(
        isLoading: Boolean,
        isFormValid: Boolean,
        isOnCardEntryForm: Boolean = true,
        cardNetwork: CardNetwork? = null
    ) {
        val buttonState = if (judo.is3DS2Enabled && isOnCardEntryForm) {
            when {
                isFormValid -> ButtonState.Enabled(continueButtonText, amount)
                else -> ButtonState.Disabled(continueButtonText, amount)
            }

        } else {
            when {
                isLoading -> ButtonState.Loading
                isFormValid -> ButtonState.Enabled(submitButtonText, amount)
                else -> ButtonState.Disabled(submitButtonText, amount)
            }
        }
        val formModel = FormModel(
            inputModel, // Model to pre fill the form
            enabledFormFields, // Fields enabled
            judo.supportedCardNetworks.toList(), // Supported networks
            cardNetwork,
            buttonState
        )
        val shouldDisplayScanButton =
            cardNetwork == null && isDependencyPresent(PAY_CARDS_DEPENDENCY) && isOnCardEntryForm
        model.postValue(CardEntryFragmentModel(formModel, shouldDisplayScanButton, isOnCardEntryForm))
    }

    private fun insert(card: TokenizedCardEntity) = viewModelScope.launch {
        cardRepository.updateAllLastUsedToFalse()
        cardRepository.insert(card)
    }
}
