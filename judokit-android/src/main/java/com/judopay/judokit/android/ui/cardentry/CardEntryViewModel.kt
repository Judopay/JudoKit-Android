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
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.formatted
import com.judopay.judokit.android.model.isTokenPayment
import com.judopay.judokit.android.model.toInputModel
import com.judopay.judokit.android.service.CardTransactionManager
import com.judopay.judokit.android.service.CardTransactionManagerResultListener
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.cardentry.model.CountryInfo
import com.judopay.judokit.android.ui.cardentry.model.FormModel
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.paymentmethods.toTokenizedCardEntity
import com.judopay.judokit.android.withWhitespacesRemoved
import kotlinx.coroutines.launch

sealed class CardEntryNavigation {
    object Card : CardEntryNavigation()

    object Billing : CardEntryNavigation()
}

data class CardEntryFragmentModel(
    val formModel: FormModel,
    val displayScanButton: Boolean = true,
    val isUserInputRequired: Boolean = true,
)

sealed class CardEntryAction {
    data class ValidationStatusChanged(val input: CardDetailsInputModel, val isFormValid: Boolean) :
        CardEntryAction()

    data class BillingDetailsValidationStatusChanged(
        val input: BillingDetailsInputModel,
        val isFormValid: Boolean,
    ) :
        CardEntryAction()

    data class InsertCard(val tokenizedCard: CardToken) : CardEntryAction()

    data class ScanCard(val result: CardScanningResult) : CardEntryAction()

    object SubmitCardEntryForm : CardEntryAction()

    object SubmitBillingDetailsForm : CardEntryAction()

    object PressBackButton : CardEntryAction()

    object SubscribeToCardTransactionManagerResults : CardEntryAction()

    object UnSubscribeToCardTransactionManagerResults : CardEntryAction()
}

internal class CardEntryViewModelFactory(
    private val judo: Judo,
    private val cardTransactionManager: CardTransactionManager,
    private val cardRepository: TokenizedCardRepository,
    private val cardEntryOptions: CardEntryOptions?,
    private val application: Application,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass == CardEntryViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            CardEntryViewModel(
                judo,
                cardTransactionManager,
                cardRepository,
                cardEntryOptions!!,
                application,
            ) as T
        } else {
            super.create(modelClass)
        }
    }
}

class CardEntryViewModel(
    private val judo: Judo,
    private val cardTransactionManager: CardTransactionManager,
    private val cardRepository: TokenizedCardRepository,
    private val cardEntryOptions: CardEntryOptions,
    application: Application,
) : AndroidViewModel(application), CardTransactionManagerResultListener {
    val model = MutableLiveData<CardEntryFragmentModel>()
    val judoPaymentResult = MutableLiveData<JudoPaymentResult>()
    val cardEntryToPaymentMethodResult = MutableLiveData<TransactionDetails.Builder>()
    val navigationObserver = MutableLiveData<CardEntryNavigation>()

    private val context = application

    // used when the form needs to be pre populated, ex. `Scan Card`
    private var cardDetailsModel = CardDetailsInputModel()
    private var billingAddressModel = BillingDetailsInputModel(countryCode = CountryInfo.currentLocaleCountry(context).numericCode)

    private var navigation: CardEntryNavigation = CardEntryNavigation.Card

    init {
        buildModel()
        navigateToBillingInfoIfNeeded()
        sendTokenRequestIfNeeded()
    }

    @Suppress("LongMethod")
    fun send(action: CardEntryAction) {
        when (action) {
            is CardEntryAction.SubscribeToCardTransactionManagerResults -> {
                cardTransactionManager.registerResultListener(this)
            }
            is CardEntryAction.UnSubscribeToCardTransactionManagerResults -> {
                cardTransactionManager.unRegisterResultListener(this)
            }
            is CardEntryAction.InsertCard -> {
                val entity = action.tokenizedCard.toTokenizedCardEntity(context, cardDetailsModel.cardHolderName)
                insert(entity)
            }
            is CardEntryAction.ValidationStatusChanged -> {
                cardDetailsModel = action.input
                buildModel(
                    isCardDetailsValid = action.isFormValid,
                    isBillingAddressValid = billingAddressModel.isValid,
                    network = cardEntryOptions.cardNetwork,
                )
            }
            is CardEntryAction.SubmitCardEntryForm -> {
                val isSaveCard = judo.paymentWidgetType == PaymentWidgetType.CREATE_CARD_TOKEN || cardEntryOptions.isAddingNewCard
                if (judo.uiConfiguration.shouldAskForBillingInformation && !isSaveCard) {
                    navigation = CardEntryNavigation.Billing
                    buildModel(
                        isCardDetailsValid = true,
                        isBillingAddressValid = billingAddressModel.isValid,
                    )
                    navigationObserver.postValue(CardEntryNavigation.Billing)
                } else {
                    buildModel(
                        isLoading = true,
                        isCardDetailsValid = true,
                    )
                    sendRequest()
                }
            }
            is CardEntryAction.SubmitBillingDetailsForm -> {
                buildModel(
                    isLoading = true,
                    isCardDetailsValid = true,
                    isBillingAddressValid = billingAddressModel.isValid,
                )
                sendRequest()
            }
            is CardEntryAction.ScanCard -> {
                cardDetailsModel = action.result.toInputModel()
                buildModel(
                    isCardDetailsValid = true,
                )
            }
            is CardEntryAction.PressBackButton -> {
                navigation = CardEntryNavigation.Card
                buildModel(
                    isCardDetailsValid = true,
                )
                navigationObserver.postValue(CardEntryNavigation.Card)
            }
            is CardEntryAction.BillingDetailsValidationStatusChanged -> {
                billingAddressModel = action.input
                buildModel(
                    isCardDetailsValid = true,
                    isBillingAddressValid = action.isFormValid,
                )
            }
        }
    }

    private fun navigateToBillingInfoIfNeeded() {
        if (!judo.isTokenPayment(cardEntryOptions) || judo.shouldAskForAdditionalCardDetails(cardEntryOptions)) {
            return
        }

        navigation = CardEntryNavigation.Billing
        buildModel(isCardDetailsValid = true)
        navigationObserver.postValue(CardEntryNavigation.Billing)
    }

    private fun sendTokenRequestIfNeeded() {
        if (judo.isUserInputRequired(cardEntryOptions)) {
            return
        }

        buildModel(isLoading = true)
        sendRequest()
    }

    // CardTransactionManagerResultListener
    override fun onCardTransactionResult(result: JudoPaymentResult) {
        judoPaymentResult.postValue(result)
        buildModel(isLoading = false)
    }

    @Suppress("LongMethod", "IllegalStateException", "UseCheckOrError")
    private fun sendRequest() =
        viewModelScope.launch {
            if (cardEntryOptions.isPresentedFromPaymentMethods && !cardEntryOptions.isAddingNewCard) {
                with(billingAddressModel) {
                    cardEntryToPaymentMethodResult.postValue(
                        TransactionDetails.Builder()
                            .setCardHolderName(cardDetailsModel.cardHolderName)
                            .setSecurityNumber(cardDetailsModel.securityNumber)
                            .setEmail(email)
                            .setCountryCode(countryCode)
                            .setPhoneCountryCode(phoneCountryCode.filter { it.isDigit() })
                            .setMobileNumber(mobileNumber.withWhitespacesRemoved)
                            .setAddressLine1(addressLine1)
                            .setAddressLine2(addressLine2)
                            .setAddressLine3(addressLine3)
                            .setCity(city)
                            .setPostalCode(postalCode)
                            .setState(state),
                    )
                }
                return@launch
            }

            val transactionDetailBuilder = TransactionDetails.Builder()

            with(cardDetailsModel) {
                transactionDetailBuilder
                    .setCardNumber(cardNumber)
                    .setCardHolderName(cardHolderName)
                    .setExpirationDate(expirationDate)
                    .setSecurityNumber(securityNumber)
            }

            with(billingAddressModel) {
                transactionDetailBuilder
                    .setEmail(email)
                    .setCountryCode(countryCode)
                    .setPhoneCountryCode(phoneCountryCode.filter { it.isDigit() })
                    .setMobileNumber(mobileNumber.withWhitespacesRemoved)
                    .setAddressLine1(addressLine1)
                    .setAddressLine2(addressLine2)
                    .setAddressLine3(addressLine3)
                    .setCity(city)
                    .setPostalCode(postalCode)
                    .setState(state)
            }

            val details = transactionDetailBuilder.build()

            when (judo.paymentWidgetType) {
                PaymentWidgetType.CARD_PAYMENT -> cardTransactionManager.payment(details, CardEntryViewModel::class.java.name)
                PaymentWidgetType.PRE_AUTH -> cardTransactionManager.preAuth(details, CardEntryViewModel::class.java.name)
                PaymentWidgetType.REGISTER_CARD -> cardTransactionManager.register(details, CardEntryViewModel::class.java.name)
                PaymentWidgetType.CHECK_CARD -> cardTransactionManager.check(details, CardEntryViewModel::class.java.name)
                PaymentWidgetType.PAYMENT_METHODS,
                PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
                PaymentWidgetType.CREATE_CARD_TOKEN,
                -> cardTransactionManager.save(details, CardEntryViewModel::class.java.name)
                PaymentWidgetType.TOKEN_PAYMENT -> {
                    cardTransactionManager.paymentWithToken(
                        getTransactionDetailsForTokenPayment(),
                        CardEntryViewModel::class.java.name,
                    )
                }
                PaymentWidgetType.TOKEN_PRE_AUTH -> {
                    cardTransactionManager.preAuthWithToken(
                        getTransactionDetailsForTokenPayment(),
                        CardEntryViewModel::class.java.name,
                    )
                }
                PaymentWidgetType.GOOGLE_PAY,
                PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
                PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
                -> throw IllegalStateException("Unsupported PaymentWidgetType")
            }
        }

    private fun getTransactionDetailsForTokenPayment(): TransactionDetails {
        val securityNumber = if (judo.uiConfiguration.shouldAskForCSC) cardDetailsModel.securityNumber else judo.cardSecurityCode
        val cardholderName =
            if (judo.uiConfiguration.shouldAskForCardholderName) cardDetailsModel.cardHolderName else judo.cardToken?.cardHolderName
        return TransactionDetails.Builder()
            .setEmail(judo.emailAddress)
            .setCountryCode(judo.address?.countryCode.toString())
            .setPhoneCountryCode(judo.phoneCountryCode)
            .setMobileNumber(judo.mobileNumber)
            .setAddressLine1(judo.address?.line1)
            .setAddressLine2(judo.address?.line2)
            .setAddressLine3(judo.address?.line3)
            .setCity(judo.address?.town)
            .setPostalCode(judo.address?.postCode)
            .setState(judo.address?.state)
            .setCardToken(judo.cardToken?.token)
            .setCardType(CardNetwork.withIdentifier(judo.cardToken?.type ?: 0, judo.cardToken?.scheme))
            .setCardLastFour(judo.cardToken?.lastFour)
            .setSecurityNumber(securityNumber)
            .setCardHolderName(cardholderName)
            .build()
    }

    @Suppress("CyclomaticComplexMethod")
    private fun buildModel(
        isLoading: Boolean = false,
        isCardDetailsValid: Boolean = false,
        isBillingAddressValid: Boolean = false,
        network: CardNetwork? = null,
    ) {
        val isInvokedFromPaymentMethods = cardEntryOptions.isPresentedFromPaymentMethods && cardEntryOptions.isAddingNewCard

        cardDetailsModel.apply {
            enabledFields = judo.enabledCardDetailsFields(cardEntryOptions)
            supportedNetworks = judo.supportedCardNetworks.toList()
            cardNetwork = network
            actionButtonState =
                if (judo.uiConfiguration.shouldAskForBillingInformation && !isInvokedFromPaymentMethods) {
                    when {
                        isCardDetailsValid -> ButtonState.Enabled(judo.continueButtonText, judo.formattedAmount)
                        else -> ButtonState.Disabled(judo.continueButtonText, judo.formattedAmount)
                    }
                } else {
                    when {
                        isLoading -> ButtonState.Loading
                        isCardDetailsValid -> ButtonState.Enabled(judo.submitButtonText(cardEntryOptions), judo.formattedAmount)
                        else -> ButtonState.Disabled(judo.submitButtonText(cardEntryOptions), judo.formattedAmount)
                    }
                }
        }

        val shouldDisplayScanButton = false
        val shouldDisplayBackButton = judo.enabledCardDetailsFields(cardEntryOptions).isNotEmpty()

        billingAddressModel.apply {
            isValid = isBillingAddressValid
            submitButtonState =
                when {
                    isLoading -> ButtonState.Loading
                    isBillingAddressValid -> ButtonState.Enabled(judo.submitButtonText(cardEntryOptions), judo.formattedAmount)
                    else -> ButtonState.Disabled(judo.submitButtonText(cardEntryOptions), judo.formattedAmount)
                }
            backButtonState =
                when {
                    shouldDisplayBackButton ->
                        if (isLoading) {
                            ButtonState.Disabled(R.string.back)
                        } else {
                            ButtonState.Enabled(R.string.back)
                        }
                    else -> ButtonState.Hidden
                }
        }

        val formModel = FormModel(cardDetailsModel, billingAddressModel)

        model.postValue(
            CardEntryFragmentModel(formModel, shouldDisplayScanButton, isUserInputRequired = judo.isUserInputRequired(cardEntryOptions)),
        )
    }

    private fun insert(card: TokenizedCardEntity) =
        viewModelScope.launch {
            cardRepository.updateAllLastUsedToFalse()
            cardRepository.insert(card)
        }
}

private fun Judo.shouldAskForAdditionalCardDetails(options: CardEntryOptions): Boolean {
    val shouldAskForCSCInPaymentMethods = options.isPresentedFromPaymentMethods && uiConfiguration.shouldPaymentMethodsVerifySecurityCode
    return uiConfiguration.shouldAskForCSC || shouldAskForCSCInPaymentMethods || uiConfiguration.shouldAskForCardholderName
}

private fun Judo.isUserInputRequired(options: CardEntryOptions): Boolean =
    if (isTokenPayment(options)) {
        uiConfiguration.shouldAskForBillingInformation || shouldAskForAdditionalCardDetails(options)
    } else {
        true
    }

private val Judo.formattedAmount: String?
    get() =
        when (paymentWidgetType) {
            PaymentWidgetType.CARD_PAYMENT,
            PaymentWidgetType.PRE_AUTH,
            PaymentWidgetType.TOKEN_PAYMENT,
            PaymentWidgetType.TOKEN_PRE_AUTH,
            ->
                if (uiConfiguration.shouldPaymentButtonDisplayAmount) {
                    amount.formatted
                } else {
                    null
                }
            else -> null
        }

private fun Judo.submitButtonText(options: CardEntryOptions): Int =
    when (paymentWidgetType) {
        PaymentWidgetType.REGISTER_CARD -> R.string.register_card
        PaymentWidgetType.CREATE_CARD_TOKEN -> R.string.save_card
        PaymentWidgetType.CHECK_CARD -> R.string.check_card
        PaymentWidgetType.CARD_PAYMENT,
        PaymentWidgetType.PRE_AUTH,
        PaymentWidgetType.TOKEN_PAYMENT,
        PaymentWidgetType.TOKEN_PRE_AUTH,
        ->
            if (uiConfiguration.shouldPaymentButtonDisplayAmount) {
                R.string.pay_amount
            } else {
                R.string.pay_now
            }
        PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
        PaymentWidgetType.PAYMENT_METHODS,
        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
        ->
            if (options.isAddingNewCard) {
                R.string.save_card
            } else {
                R.string.pay_now
            }
        PaymentWidgetType.GOOGLE_PAY,
        PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
        -> R.string.empty
    }

private val Judo.continueButtonText: Int
    get() =
        when (paymentWidgetType) {
            PaymentWidgetType.CREATE_CARD_TOKEN -> R.string.save_card
            PaymentWidgetType.REGISTER_CARD,
            PaymentWidgetType.CHECK_CARD,
            PaymentWidgetType.CARD_PAYMENT,
            PaymentWidgetType.PRE_AUTH,
            PaymentWidgetType.TOKEN_PAYMENT,
            PaymentWidgetType.TOKEN_PRE_AUTH,
            -> R.string.continue_text
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
            PaymentWidgetType.PAYMENT_METHODS,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
            ->
                if (uiConfiguration.shouldAskForBillingInformation) {
                    R.string.continue_text
                } else {
                    R.string.save_card
                }
            PaymentWidgetType.GOOGLE_PAY,
            PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
            -> R.string.empty
        }

private fun Judo.isTokenPayment(options: CardEntryOptions): Boolean {
    val isPaymentMethodsTokenPayment = options.isPresentedFromPaymentMethods && !options.isAddingNewCard
    return paymentWidgetType.isTokenPayment || isPaymentMethodsTokenPayment
}

private fun Judo.enabledCardDetailsFields(options: CardEntryOptions): List<CardDetailsFieldType> =
    if (isTokenPayment(options)) {
        val shouldAskForCSCInPaymentMethods =
            options.isPresentedFromPaymentMethods &&
                uiConfiguration.shouldPaymentMethodsVerifySecurityCode
        val shouldAskForCSC = uiConfiguration.shouldAskForCSC || shouldAskForCSCInPaymentMethods

        listOfNotNull(
            CardDetailsFieldType.SECURITY_NUMBER.takeIf { shouldAskForCSC },
            CardDetailsFieldType.HOLDER_NAME.takeIf { uiConfiguration.shouldAskForCardholderName },
        )
    } else {
        val avsEnabled = uiConfiguration.avsEnabled && !uiConfiguration.shouldAskForBillingInformation

        listOfNotNull(
            CardDetailsFieldType.NUMBER,
            CardDetailsFieldType.HOLDER_NAME,
            CardDetailsFieldType.EXPIRATION_DATE,
            CardDetailsFieldType.SECURITY_NUMBER,
            CardDetailsFieldType.COUNTRY.takeIf { avsEnabled },
            CardDetailsFieldType.POST_CODE.takeIf { avsEnabled },
        )
    }
