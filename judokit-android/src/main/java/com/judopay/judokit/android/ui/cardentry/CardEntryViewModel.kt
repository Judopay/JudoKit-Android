package com.judopay.judokit.android.ui.cardentry

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.service.ThreeDSChallengeDelegate
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.cardentry.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormModel
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.paymentmethods.toTokenizedCardEntity
import com.judopay.judokit.android.withWhitespacesRemoved
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class CardEntryNavigation {
    data object Card : CardEntryNavigation()

    data object Billing : CardEntryNavigation()
}

data class CardEntryFragmentModel(
    val formModel: FormModel,
    val isUserInputRequired: Boolean = true,
)

sealed class CardEntryAction {
    data class ValidationStatusChanged(
        val input: CardDetailsInputModel,
        val isFormValid: Boolean,
    ) : CardEntryAction()

    data class BillingDetailsValidationStatusChanged(
        val input: BillingDetailsInputModel,
        val isFormValid: Boolean,
    ) : CardEntryAction()

    data class InsertCard(
        val tokenizedCard: CardToken,
    ) : CardEntryAction()

    data class ScanCard(
        val result: CardScanningResult,
    ) : CardEntryAction()

    object SubmitCardEntryForm : CardEntryAction()

    object SubmitBillingDetailsForm : CardEntryAction()

    object PressBackButton : CardEntryAction()

    object Initialize : CardEntryAction()
}

@Suppress("DEPRECATION")
class CardEntryViewModel
    internal constructor(
        private val judo: Judo,
        private val cardTransactionRepository: CardTransactionRepository,
        private val cardRepository: TokenizedCardRepository,
        private val cardEntryOptions: CardEntryOptions,
        application: Application,
    ) : AndroidViewModel(application) {
        private val _uiState = MutableStateFlow<CardEntryFragmentModel?>(null)
        val uiState: StateFlow<CardEntryFragmentModel?> = _uiState.asStateFlow()

        private val _paymentResultEffect = MutableSharedFlow<JudoPaymentResult>(extraBufferCapacity = 1)
        val paymentResultEffect: SharedFlow<JudoPaymentResult> = _paymentResultEffect

        private val _cardEntryToPaymentMethodResultEffect = MutableSharedFlow<TransactionDetails.Builder>(extraBufferCapacity = 1)
        val cardEntryToPaymentMethodResultEffect: SharedFlow<TransactionDetails.Builder> = _cardEntryToPaymentMethodResultEffect

        private val _navigationEffect = MutableSharedFlow<CardEntryNavigation>(extraBufferCapacity = 1)
        val navigationEffect: SharedFlow<CardEntryNavigation> = _navigationEffect

        private val threeDSDelegate = ThreeDSChallengeDelegate()

        /** Emits the active 3DS2 challenge data while a challenge is in progress, null otherwise. */
        internal val pendingChallenge: StateFlow<com.judopay.judokit.android.service.ChallengeData?> = threeDSDelegate.pendingChallenge

        private val context = application

        // used when the form needs to be pre populated, ex. `Scan Card`
        private var cardDetailsModel = CardDetailsInputModel()
        private var billingAddressModel = BillingDetailsInputModel(countryCode = Country.currentLocaleCountry(context).numericCode)

        private var navigation: CardEntryNavigation = CardEntryNavigation.Card
        private var isInitialized = false

        init {
            buildModel()
        }

        @Suppress("LongMethod", "CyclomaticComplexMethod")
        fun send(action: CardEntryAction) {
            when (action) {
                is CardEntryAction.Initialize -> {
                    if (!isInitialized) {
                        isInitialized = true
                        navigateToBillingInfoIfNeeded()
                        sendTokenRequestIfNeeded()
                    }
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
                        if (!_navigationEffect.tryEmit(CardEntryNavigation.Billing)) {
                            Log.w(TAG, "Navigation effect dropped")
                        }
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
                    if (!_navigationEffect.tryEmit(CardEntryNavigation.Card)) {
                        Log.w(TAG, "Navigation effect dropped")
                    }
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

        /**
         * Called by the Fragment after the native 3DS2 challenge completes.
         * Clears the pending challenge state so subsequent config-change re-subscriptions
         * do not re-trigger doChallenge. The first call wins; any duplicate call from
         * a stale receiver is silently dropped because the channel already has a value.
         */
        fun onChallengeResult(status: String?) = threeDSDelegate.onChallengeResult(status)

        private fun navigateToBillingInfoIfNeeded() {
            if (!judo.isTokenPayment(cardEntryOptions) || judo.shouldAskForAdditionalCardDetails(cardEntryOptions)) {
                return
            }

            navigation = CardEntryNavigation.Billing
            buildModel(isCardDetailsValid = true)
            if (!_navigationEffect.tryEmit(CardEntryNavigation.Billing)) {
                Log.w(TAG, "Navigation effect dropped")
            }
        }

        private fun sendTokenRequestIfNeeded() {
            if (judo.isUserInputRequired(cardEntryOptions)) {
                return
            }

            buildModel(isLoading = true)
            sendRequest()
        }

        @Suppress("LongMethod", "CyclomaticComplexMethod", "IllegalStateException", "UseCheckOrError")
        private fun sendRequest() =
            viewModelScope.launch {
                if (cardEntryOptions.isPresentedFromPaymentMethods && !cardEntryOptions.isAddingNewCard) {
                    with(billingAddressModel) {
                        val builder =
                            TransactionDetails
                                .Builder()
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
                                .setAdministrativeDivision(administrativeDivision)
                        if (!_cardEntryToPaymentMethodResultEffect.tryEmit(builder)) {
                            Log.w(TAG, "Card-entry-to-payment-method result effect dropped")
                        }
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
                        .setAdministrativeDivision(administrativeDivision)
                }

                val details = transactionDetailBuilder.build()

                val result =
                    when (judo.paymentWidgetType) {
                        PaymentWidgetType.CARD_PAYMENT -> cardTransactionRepository.payment(details, threeDSDelegate.challengeRunner)
                        PaymentWidgetType.PRE_AUTH -> cardTransactionRepository.preAuth(details, threeDSDelegate.challengeRunner)
                        PaymentWidgetType.CHECK_CARD -> cardTransactionRepository.check(details, threeDSDelegate.challengeRunner)
                        PaymentWidgetType.PAYMENT_METHODS,
                        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
                        PaymentWidgetType.CREATE_CARD_TOKEN,
                        -> cardTransactionRepository.save(details, threeDSDelegate.challengeRunner)
                        PaymentWidgetType.TOKEN_PAYMENT ->
                            cardTransactionRepository.paymentWithToken(
                                getTransactionDetailsForTokenPayment(),
                                threeDSDelegate.challengeRunner,
                            )
                        PaymentWidgetType.TOKEN_PRE_AUTH ->
                            cardTransactionRepository.preAuthWithToken(
                                getTransactionDetailsForTokenPayment(),
                                threeDSDelegate.challengeRunner,
                            )
                        PaymentWidgetType.GOOGLE_PAY,
                        PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
                        PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
                        -> throw IllegalStateException("Unsupported PaymentWidgetType")
                    }

                buildModel(isLoading = false)
                if (!_paymentResultEffect.tryEmit(result)) {
                    Log.w(TAG, "Payment result effect dropped: $result")
                }
            }

        private fun getTransactionDetailsForTokenPayment(): TransactionDetails {
            val securityNumber = if (judo.uiConfiguration.shouldAskForCSC) cardDetailsModel.securityNumber else judo.cardSecurityCode
            val cardholderName =
                if (judo.uiConfiguration.shouldAskForCardholderName) cardDetailsModel.cardHolderName else judo.cardToken?.cardHolderName
            return TransactionDetails
                .Builder()
                .setEmail(judo.emailAddress)
                .setCountryCode(judo.address?.countryCode.toString())
                .setPhoneCountryCode(judo.phoneCountryCode)
                .setMobileNumber(judo.mobileNumber)
                .setAddressLine1(judo.address?.line1)
                .setAddressLine2(judo.address?.line2)
                .setAddressLine3(judo.address?.line3)
                .setCity(judo.address?.town)
                .setPostalCode(judo.address?.postCode)
                .setAdministrativeDivision(judo.address?.administrativeDivision)
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

            cardDetailsModel =
                cardDetailsModel.copy(
                    enabledFields = judo.enabledCardDetailsFields(cardEntryOptions),
                    supportedNetworks = judo.supportedCardNetworks.toList(),
                    cardNetwork = network,
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
                        },
                )

            val shouldDisplayBackButton = judo.enabledCardDetailsFields(cardEntryOptions).isNotEmpty()

            billingAddressModel =
                billingAddressModel.copy(
                    isValid = isBillingAddressValid,
                    submitButtonState =
                        when {
                            isLoading -> ButtonState.Loading
                            isBillingAddressValid -> ButtonState.Enabled(judo.submitButtonText(cardEntryOptions), judo.formattedAmount)
                            else -> ButtonState.Disabled(judo.submitButtonText(cardEntryOptions), judo.formattedAmount)
                        },
                    backButtonState =
                        when {
                            shouldDisplayBackButton ->
                                if (isLoading) {
                                    ButtonState.Disabled(R.string.jp_back)
                                } else {
                                    ButtonState.Enabled(R.string.jp_back)
                                }
                            else -> ButtonState.Hidden
                        },
                )

            val formModel = FormModel(cardDetailsModel, billingAddressModel)

            _uiState.value = CardEntryFragmentModel(formModel, isUserInputRequired = judo.isUserInputRequired(cardEntryOptions))
        }

        private fun insert(card: TokenizedCardEntity) =
            viewModelScope.launch {
                withContext(NonCancellable) {
                    cardRepository.updateAllLastUsedToFalse()
                    cardRepository.insert(card)
                }
            }

        companion object {
            private val TAG = CardEntryViewModel::class.java.name
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
        PaymentWidgetType.CREATE_CARD_TOKEN -> R.string.jp_save_card
        PaymentWidgetType.CHECK_CARD -> R.string.jp_check_card
        PaymentWidgetType.CARD_PAYMENT,
        PaymentWidgetType.PRE_AUTH,
        PaymentWidgetType.TOKEN_PAYMENT,
        PaymentWidgetType.TOKEN_PRE_AUTH,
        ->
            if (uiConfiguration.shouldPaymentButtonDisplayAmount) {
                R.string.jp_pay_amount
            } else {
                R.string.jp_pay_now
            }
        PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
        PaymentWidgetType.PAYMENT_METHODS,
        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
        ->
            if (options.isAddingNewCard) {
                R.string.jp_save_card
            } else {
                R.string.jp_pay_now
            }
        PaymentWidgetType.GOOGLE_PAY,
        PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
        -> R.string.jp_empty
    }

private val Judo.continueButtonText: Int
    get() =
        when (paymentWidgetType) {
            PaymentWidgetType.CREATE_CARD_TOKEN -> R.string.jp_save_card
            PaymentWidgetType.CHECK_CARD,
            PaymentWidgetType.CARD_PAYMENT,
            PaymentWidgetType.PRE_AUTH,
            PaymentWidgetType.TOKEN_PAYMENT,
            PaymentWidgetType.TOKEN_PRE_AUTH,
            -> R.string.jp_continue_text
            PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
            PaymentWidgetType.PAYMENT_METHODS,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
            ->
                if (uiConfiguration.shouldAskForBillingInformation) {
                    R.string.jp_continue_text
                } else {
                    R.string.jp_save_card
                }
            PaymentWidgetType.GOOGLE_PAY,
            PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
            -> R.string.jp_empty
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
