package com.judopay.judokit.android.ui.cardentry

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.PendingChallenge
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import com.judopay.judokit.android.db.repository.TokenizedCardRepository
import com.judopay.judokit.android.model.AVSCountry
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.CardScanningResult
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.asAVSCountry
import com.judopay.judokit.android.model.formatted
import com.judopay.judokit.android.model.isTokenPayment
import com.judopay.judokit.android.model.toInputModel
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.service.ChallengeRunner
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.BillingDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsFieldType
import com.judopay.judokit.android.ui.cardentry.model.CardDetailsInputModel
import com.judopay.judokit.android.ui.cardentry.model.CardEntryOptions
import com.judopay.judokit.android.ui.cardentry.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.model.FormModel
import com.judopay.judokit.android.ui.cardentry.validation.BillingDetailsFormValidator
import com.judopay.judokit.android.ui.cardentry.validation.CardDetailsFormValidator
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import com.judopay.judokit.android.ui.common.ButtonState
import com.judopay.judokit.android.ui.common.PaymentResultEmitter
import com.judopay.judokit.android.ui.paymentmethods.toTokenizedCardEntity
import com.judopay.judokit.android.withWhitespacesRemoved
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
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

internal sealed class CardEntryAction {
    data class CardFieldChanged(
        val fieldType: CardDetailsFieldType,
        val value: String,
        val event: FormFieldEvent,
    ) : CardEntryAction()

    data class BillingFieldChanged(
        val fieldType: BillingDetailsFieldType,
        val value: String,
        val event: FormFieldEvent,
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

    /**
     * A 3DS2 challenge that was showing when the process died has ended. The coroutine that
     * launched it died with the process, so the transaction is completed from [pending].
     */
    internal data class CompleteRestoredChallenge(
        val pending: PendingChallenge,
        val challengeStatus: String,
    ) : CardEntryAction()
}

@Suppress("DEPRECATION", "LongParameterList")
internal class CardEntryViewModel
    internal constructor(
        private val judo: Judo,
        private val cardTransactionRepository: CardTransactionRepository,
        private val cardRepository: TokenizedCardRepository,
        private val cardEntryOptions: CardEntryOptions,
        application: Application,
        private val cardDetailsFormValidator: CardDetailsFormValidator =
            CardDetailsFormValidator(judo.supportedCardNetworks.toList()),
        private val billingDetailsFormValidator: BillingDetailsFormValidator =
            BillingDetailsFormValidator(),
        private val challengeRunner: ChallengeRunner,
        isResumingChallenge: Boolean = false,
    ) : AndroidViewModel(application) {
        private val _uiState = MutableStateFlow<CardEntryFragmentModel?>(null)
        val uiState: StateFlow<CardEntryFragmentModel?> = _uiState.asStateFlow()

        private val paymentResultEmitter = PaymentResultEmitter(TAG)

        /** The payment results for the Fragment to collect; see [PaymentResultEmitter]. */
        internal val paymentResults: Flow<JudoPaymentResult> = paymentResultEmitter.results

        private val _cardEntryToPaymentMethodResultEffect = MutableSharedFlow<TransactionDetails.Builder>(extraBufferCapacity = 1)
        val cardEntryToPaymentMethodResultEffect: SharedFlow<TransactionDetails.Builder> = _cardEntryToPaymentMethodResultEffect

        private val _navigationEffect = MutableSharedFlow<CardEntryNavigation>(extraBufferCapacity = 1)
        val navigationEffect: SharedFlow<CardEntryNavigation> = _navigationEffect

        private val context = application

        private var cardDetailsModel = CardDetailsInputModel()
        private var billingAddressModel = BillingDetailsInputModel(countryCode = Country.currentLocaleCountry(context).numericCode)

        /*
         * Per-field validity state used for isCardFormValid() / isBillingFormValid().
         * Key absent = not yet validated (treated as invalid).
         */
        private val cardFieldValidity = mutableMapOf<CardDetailsFieldType, Boolean>()
        private val billingFieldValidity = mutableMapOf<BillingDetailsFieldType, Boolean>()

        // Per-field display errors that go into the model for the View to render.
        private val cardFieldDisplayErrors = mutableMapOf<CardDetailsFieldType, Int?>()
        private val billingFieldDisplayErrors = mutableMapOf<BillingDetailsFieldType, Int?>()

        private val _navigationState = MutableStateFlow<CardEntryNavigation>(CardEntryNavigation.Card)
        val navigationState: StateFlow<CardEntryNavigation> = _navigationState.asStateFlow()
        private var isInitialized = false

        init {
            // Initialize the card AVS country so PostcodeValidator uses the right regex from the start.
            cardDetailsFormValidator.country = cardDetailsModel.country.asAVSCountry() ?: AVSCountry.OTHER

            // AVS country is always valid (user chooses among pre-selected values).
            cardFieldValidity[CardDetailsFieldType.COUNTRY] = true

            // Initialize the billing country so country-aware validators are ready.
            val initialBillingCountry = Country.list(context).firstOrNull { it.numericCode == billingAddressModel.countryCode }
            billingDetailsFormValidator.country = initialBillingCountry

            // Fields that are always valid when empty (no validators assigned or treated as optional).
            billingFieldValidity[BillingDetailsFieldType.ADDRESS_LINE_2] = true
            billingFieldValidity[BillingDetailsFieldType.ADDRESS_LINE_3] = true
            billingFieldValidity[BillingDetailsFieldType.PHONE_COUNTRY_CODE] = true
            billingFieldValidity[BillingDetailsFieldType.MOBILE_NUMBER] = true

            billingAddressModel =
                billingAddressModel.copy(
                    adminDivisionRequired = billingDetailsFormValidator.adminDivisionRequired,
                )

            if (isResumingChallenge) {
                // The process died during a 3DS2 challenge. Its outcome arrives through the
                // Fragment's challenge launcher; until then, show the transaction as in progress and
                // never start a new one (a token payment would otherwise be sent again on Initialize).
                isInitialized = true
                buildModel(isLoading = true)
            } else {
                buildModel()
            }
        }

        @Suppress("LongMethod", "CyclomaticComplexMethod")
        internal fun send(action: CardEntryAction) {
            when (action) {
                is CardEntryAction.Initialize -> {
                    if (!isInitialized) {
                        isInitialized = true
                        navigateToBillingInfoIfNeeded()
                        sendTokenRequestIfNeeded()
                    }
                }
                is CardEntryAction.InsertCard -> {
                    // The form is empty for a challenge restored after process death: fall back to the token's name.
                    val cardholderName = cardDetailsModel.cardHolderName.ifBlank { action.tokenizedCard.cardHolderName.orEmpty() }
                    val entity = action.tokenizedCard.toTokenizedCardEntity(context, cardholderName)
                    insert(entity)
                }
                is CardEntryAction.CardFieldChanged -> handleCardFieldChanged(action)
                is CardEntryAction.BillingFieldChanged -> handleBillingFieldChanged(action)
                is CardEntryAction.SubmitCardEntryForm -> {
                    val isSaveCard = judo.paymentWidgetType == PaymentWidgetType.CREATE_CARD_TOKEN || cardEntryOptions.isAddingNewCard
                    if (judo.uiConfiguration.shouldAskForBillingInformation && !isSaveCard) {
                        _navigationState.value = CardEntryNavigation.Billing
                        buildModel(
                            isCardDetailsValid = true,
                            isBillingAddressValid = isBillingFormValid(),
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
                        isBillingAddressValid = isBillingFormValid(),
                    )
                    sendRequest()
                }
                is CardEntryAction.ScanCard -> {
                    cardDetailsModel = action.result.toInputModel()
                    buildModel(isCardDetailsValid = true)
                }
                is CardEntryAction.CompleteRestoredChallenge -> completeRestoredChallenge(action)
                is CardEntryAction.PressBackButton -> {
                    _navigationState.value = CardEntryNavigation.Card
                    buildModel(isCardDetailsValid = true)
                    if (!_navigationEffect.tryEmit(CardEntryNavigation.Card)) {
                        Log.w(TAG, "Navigation effect dropped")
                    }
                }
            }
        }

        private fun completeRestoredChallenge(action: CardEntryAction.CompleteRestoredChallenge) =
            viewModelScope.launch {
                val pending = action.pending
                val result = cardTransactionRepository.complete3ds2(pending.receiptId, pending.messageVersion, null, action.challengeStatus)
                buildModel(isLoading = false)
                paymentResultEmitter.emit(result)
            }

        private fun handleCardFieldChanged(action: CardEntryAction.CardFieldChanged) {
            val type = action.fieldType
            val value = action.value
            val event = action.event

            cardDetailsModel = cardDetailsModel.withUpdatedField(type, value)

            // Detect card network change on the NUMBER field and cascade to security code.
            var networkChanged = false
            if (type == CardDetailsFieldType.NUMBER) {
                val newNetwork = CardNetwork.ofNumber(value.withWhitespacesRemoved)
                if (cardDetailsFormValidator.cardNetwork != newNetwork) {
                    cardDetailsFormValidator.cardNetwork = newNetwork
                    networkChanged = true
                }
            }

            // Update AVS country for postcode validation when the country field changes.
            if (type == CardDetailsFieldType.COUNTRY) {
                cardDetailsFormValidator.country = value.asAVSCountry() ?: AVSCountry.OTHER
            }

            val result = cardDetailsFormValidator.validateField(type, value, event)
            applyCardValidationResult(type, result, event)

            if (networkChanged) {
                val cvvValue = cardDetailsModel.securityNumber
                val cvvEvent =
                    if (cvvValue.isNotEmpty() || cardFieldDisplayErrors[CardDetailsFieldType.SECURITY_NUMBER] != null) {
                        FormFieldEvent.FOCUS_CHANGED
                    } else {
                        FormFieldEvent.TEXT_CHANGED
                    }
                val cvvResult =
                    cardDetailsFormValidator.validateField(
                        CardDetailsFieldType.SECURITY_NUMBER,
                        cvvValue,
                        cvvEvent,
                    )
                applyCardValidationResult(CardDetailsFieldType.SECURITY_NUMBER, cvvResult, cvvEvent)
                cardDetailsModel = cardDetailsModel.copy(cardNetwork = cardDetailsFormValidator.cardNetwork)
            }

            cardDetailsModel = cardDetailsModel.copy(fieldErrors = cardFieldDisplayErrors.toMap())

            buildModel(
                isCardDetailsValid = isCardFormValid(),
                isBillingAddressValid = isBillingFormValid(),
                network = cardDetailsModel.cardNetwork,
            )
        }

        private fun applyCardValidationResult(
            type: CardDetailsFieldType,
            result: ValidationResult?,
            event: FormFieldEvent,
        ) {
            cardFieldValidity[type] = result?.isValid ?: true
            when {
                result == null || result.isValid -> cardFieldDisplayErrors[type] = null
                event == FormFieldEvent.FOCUS_CHANGED ->
                    cardFieldDisplayErrors[type] = result.message.takeIf { it != R.string.jp_empty }
            }
        }

        @Suppress("CyclomaticComplexity")
        private fun handleBillingFieldChanged(action: CardEntryAction.BillingFieldChanged) {
            val type = action.fieldType
            val value = action.value
            val event = action.event

            when (type) {
                BillingDetailsFieldType.COUNTRY -> {
                    val country = Country.list(context).firstOrNull { it.name.equals(value, ignoreCase = true) }
                    billingDetailsFormValidator.country = country
                    billingAddressModel =
                        billingAddressModel.copy(
                            countryCode = country?.numericCode ?: "",
                            adminDivisionRequired = billingDetailsFormValidator.adminDivisionRequired,
                        )

                    billingFieldValidity.remove(BillingDetailsFieldType.ADMINISTRATIVE_DIVISION)
                    billingFieldDisplayErrors.remove(BillingDetailsFieldType.ADMINISTRATIVE_DIVISION)
                }
                BillingDetailsFieldType.ADMINISTRATIVE_DIVISION -> {
                    val isoCode = billingDetailsFormValidator.isoCodeForAdminDivision(value) ?: ""
                    billingAddressModel = billingAddressModel.copy(administrativeDivision = isoCode)
                }
                else -> billingAddressModel = billingAddressModel.withUpdatedField(type, value)
            }

            val result =
                billingDetailsFormValidator.validateField(
                    type,
                    value,
                    event,
                    billingAddressModel.phoneCountryCode,
                    billingAddressModel.mobileNumber,
                )
            applyBillingValidationResult(type, result, event)

            billingAddressModel = billingAddressModel.copy(fieldErrors = billingFieldDisplayErrors.toMap())

            buildModel(
                isCardDetailsValid = isCardFormValid(),
                isBillingAddressValid = isBillingFormValid(),
            )
        }

        private fun applyBillingValidationResult(
            type: BillingDetailsFieldType,
            result: ValidationResult?,
            event: FormFieldEvent,
        ) {
            billingFieldValidity[type] = result?.isValid ?: true
            when {
                result == null || result.isValid -> billingFieldDisplayErrors[type] = null
                event == FormFieldEvent.FOCUS_CHANGED ->
                    billingFieldDisplayErrors[type] = result.message.takeIf { it != R.string.jp_empty }
            }
        }

        private fun isCardFormValid(): Boolean {
            val enabled = cardDetailsModel.enabledFields
            return enabled.isNotEmpty() && enabled.all { cardFieldValidity[it] == true }
        }

        private fun isBillingFormValid(): Boolean =
            BillingDetailsFieldType.entries.all { type ->
                if (type == BillingDetailsFieldType.ADMINISTRATIVE_DIVISION && !billingDetailsFormValidator.adminDivisionRequired) {
                    return@all true
                }
                billingFieldValidity[type] == true
            }

        private fun navigateToBillingInfoIfNeeded() {
            if (!judo.isTokenPayment(cardEntryOptions) || judo.shouldAskForAdditionalCardDetails(cardEntryOptions)) {
                return
            }

            _navigationState.value = CardEntryNavigation.Billing
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
                        PaymentWidgetType.CARD_PAYMENT -> cardTransactionRepository.payment(details, challengeRunner)
                        PaymentWidgetType.PRE_AUTH -> cardTransactionRepository.preAuth(details, challengeRunner)
                        PaymentWidgetType.CHECK_CARD -> cardTransactionRepository.check(details, challengeRunner)
                        PaymentWidgetType.PAYMENT_METHODS,
                        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
                        PaymentWidgetType.CREATE_CARD_TOKEN,
                        -> cardTransactionRepository.save(details, challengeRunner)
                        PaymentWidgetType.TOKEN_PAYMENT ->
                            cardTransactionRepository.paymentWithToken(
                                getTransactionDetailsForTokenPayment(),
                                challengeRunner,
                            )
                        PaymentWidgetType.TOKEN_PRE_AUTH ->
                            cardTransactionRepository.preAuthWithToken(
                                getTransactionDetailsForTokenPayment(),
                                challengeRunner,
                            )
                        PaymentWidgetType.GOOGLE_PAY,
                        PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
                        PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS,
                        -> throw IllegalStateException("Unsupported PaymentWidgetType")
                    }

                buildModel(isLoading = false)
                paymentResultEmitter.emit(result)
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
                                isCardDetailsValid -> ButtonState.Enabled(judo.continueButtonText, judo.formattedAmount(cardEntryOptions))
                                else -> ButtonState.Disabled(judo.continueButtonText, judo.formattedAmount(cardEntryOptions))
                            }
                        } else {
                            when {
                                isLoading -> ButtonState.Loading
                                isCardDetailsValid ->
                                    ButtonState.Enabled(
                                        judo.submitButtonText(cardEntryOptions),
                                        judo.formattedAmount(cardEntryOptions),
                                    )
                                else ->
                                    ButtonState.Disabled(
                                        judo.submitButtonText(cardEntryOptions),
                                        judo.formattedAmount(cardEntryOptions),
                                    )
                            }
                        },
                )

            val shouldDisplayBackButton = judo.enabledCardDetailsFields(cardEntryOptions).isNotEmpty()

            billingAddressModel =
                billingAddressModel.copy(
                    submitButtonState =
                        when {
                            isLoading -> ButtonState.Loading
                            isBillingAddressValid ->
                                ButtonState.Enabled(
                                    judo.submitButtonText(cardEntryOptions),
                                    judo.formattedAmount(cardEntryOptions),
                                )
                            else -> ButtonState.Disabled(judo.submitButtonText(cardEntryOptions), judo.formattedAmount(cardEntryOptions))
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

private fun CardDetailsInputModel.withUpdatedField(
    type: CardDetailsFieldType,
    value: String,
): CardDetailsInputModel =
    when (type) {
        CardDetailsFieldType.NUMBER -> copy(cardNumber = value)
        CardDetailsFieldType.HOLDER_NAME -> copy(cardHolderName = value)
        CardDetailsFieldType.EXPIRATION_DATE -> copy(expirationDate = value)
        CardDetailsFieldType.SECURITY_NUMBER -> copy(securityNumber = value)
        CardDetailsFieldType.COUNTRY -> copy(country = value)
        CardDetailsFieldType.POST_CODE -> copy(postCode = value)
    }

private fun BillingDetailsInputModel.withUpdatedField(
    type: BillingDetailsFieldType,
    value: String,
): BillingDetailsInputModel =
    when (type) {
        BillingDetailsFieldType.EMAIL -> copy(email = value)
        BillingDetailsFieldType.PHONE_COUNTRY_CODE -> copy(phoneCountryCode = value)
        BillingDetailsFieldType.MOBILE_NUMBER -> copy(mobileNumber = value)
        BillingDetailsFieldType.ADDRESS_LINE_1 -> copy(addressLine1 = value)
        BillingDetailsFieldType.ADDRESS_LINE_2 -> copy(addressLine2 = value)
        BillingDetailsFieldType.ADDRESS_LINE_3 -> copy(addressLine3 = value)
        BillingDetailsFieldType.CITY -> copy(city = value)
        BillingDetailsFieldType.POST_CODE -> copy(postalCode = value)
        // COUNTRY and ADMINISTRATIVE_DIVISION are handled separately in the action handler.
        else -> this
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

private fun Judo.formattedAmount(options: CardEntryOptions): String? =
    when (paymentWidgetType) {
        PaymentWidgetType.CARD_PAYMENT,
        PaymentWidgetType.PRE_AUTH,
        PaymentWidgetType.TOKEN_PAYMENT,
        PaymentWidgetType.TOKEN_PRE_AUTH,
        ->
            if (uiConfiguration.shouldPaymentButtonDisplayAmount) amount.formatted else null
        PaymentWidgetType.PAYMENT_METHODS,
        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
        ->
            if (!options.isAddingNewCard && uiConfiguration.shouldPaymentButtonDisplayAmount) amount.formatted else null
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
        PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS ->
            if (options.isAddingNewCard) R.string.jp_save_card else R.string.jp_pay_now
        PaymentWidgetType.PAYMENT_METHODS,
        PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS,
        ->
            when {
                options.isAddingNewCard -> R.string.jp_save_card
                uiConfiguration.shouldPaymentButtonDisplayAmount -> R.string.jp_pay_amount
                else -> R.string.jp_pay_now
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
