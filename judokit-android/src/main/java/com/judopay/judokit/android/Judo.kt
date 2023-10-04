package com.judopay.judokit.android

import android.app.Activity
import android.os.Parcelable
import com.judopay.judokit.android.api.model.Authorization
import com.judopay.judokit.android.api.model.request.Address
import com.judopay.judokit.android.api.model.response.CardToken
import com.judopay.judokit.android.model.Amount
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.Currency
import com.judopay.judokit.android.model.GooglePayConfiguration
import com.judopay.judokit.android.model.NetworkTimeout
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.PrimaryAccountDetails
import com.judopay.judokit.android.model.Reference
import com.judopay.judokit.android.model.ScaExemption
import com.judopay.judokit.android.model.SubProductInfo
import com.judopay.judokit.android.model.UiConfiguration
import com.judopay.judokit.android.ui.common.REGEX_JUDO_ID
import kotlinx.parcelize.Parcelize

/**
 * The wrapper for providing data to Activity and Fragments classes in the SDK (e.g. JudopayActivity).
 * <p>
 * Use the {@link Judo.Builder class for constructing} an instance of {@link Judo}.
 * When calling an Activity with an Intent extra or a Fragment using an arguments Bundle,
 * use {@link Judo#JUDO_OPTIONS} as the extra or argument name.
 */

// Bundle keys
const val JUDO_OPTIONS = "com.judopay.judokit.android.options"
const val JUDO_RESULT = "com.judopay.judokit.android.result"
const val JUDO_ERROR = "com.judopay.judokit.android.error"

// Result codes
/** Judo activity result: operation succeeded.  */
const val PAYMENT_SUCCESS = Activity.RESULT_FIRST_USER + 1

/** Judo activity result: operation canceled.  */
const val PAYMENT_CANCELLED = Activity.RESULT_FIRST_USER + 2

/** Judo activity result: operation error  */
const val PAYMENT_ERROR = Activity.RESULT_FIRST_USER + 3

/** Default 3DS 2.0 maximum timeout value */
private const val DEFAULT_MAX_TIMEOUT = 120

/** Default 3DS 2.0 protocol message version */
private const val THREE_DS_TWO_MESSAGE_VERSION_TWO_DOT_TWO = "2.2.0"

/**
 * Judo configuration object that is required for initiating a payment.
 */
@Parcelize
class Judo internal constructor(
    val judoId: String,
    val authorization: Authorization,
    val isSandboxed: Boolean,
    val amount: Amount,
    val reference: Reference,
    val uiConfiguration: UiConfiguration,
    val paymentMethods: Array<PaymentMethod>,
    val supportedCardNetworks: Array<CardNetwork>,
    val primaryAccountDetails: PrimaryAccountDetails?,
    val googlePayConfiguration: GooglePayConfiguration?,
    val paymentWidgetType: PaymentWidgetType,
    val address: Address?,
    val initialRecurringPayment: Boolean?,
    val networkTimeout: NetworkTimeout,
    val challengeRequestIndicator: ChallengeRequestIndicator?,
    val scaExemption: ScaExemption?,
    val mobileNumber: String?,
    val phoneCountryCode: String?,
    val emailAddress: String?,
    val threeDSTwoMaxTimeout: Int,
    val threeDSTwoMessageVersion: String,
    val delayedAuthorisation: Boolean? = null,
    val cardToken: CardToken? = null,
    val cardSecurityCode: String? = null,
    val subProductInfo: SubProductInfo = SubProductInfo.Unknown
) : Parcelable {

    /**
     * Builder class to create a [Judo] object since it's constructor is private
     * @param paymentWidgetType Property used to decide which payment widget to invoke for a transaction
     */
    class Builder(private val paymentWidgetType: PaymentWidgetType) {
        private var judoId: String? = null
        private var authorization: Authorization? = null
        private var isSandboxed: Boolean? = null
        private var amount: Amount? = null
        private var reference: Reference? = null
        private var uiConfiguration: UiConfiguration? = UiConfiguration.Builder().build()
        private var paymentMethods: Array<PaymentMethod>? = null
        private var supportedCardNetworks: Array<CardNetwork>? = null
        private var primaryAccountDetails: PrimaryAccountDetails? = null
        private var googlePayConfiguration: GooglePayConfiguration? = null
        private var address: Address? = null
        private var initialRecurringPayment: Boolean? = null
        private var networkTimeout: NetworkTimeout? = null
        private var challengeRequestIndicator: ChallengeRequestIndicator? = null
        private var scaExemption: ScaExemption? = null
        private var mobileNumber: String? = null
        private var phoneCountryCode: String? = null
        private var emailAddress: String? = null
        private var threeDSTwoMaxTimeout: Int? = null
        private var threeDSTwoMessageVersion: String? = null
        private var delayedAuthorisation: Boolean? = null
        private var cardToken: CardToken? = null
        private var cardSecurityCode: String? = null
        private var subProductInfo: SubProductInfo = SubProductInfo.Unknown

        /**
         * Sets the unique merchant ID
         * @param id Merchant unique identifier provided by Judo
         */
        fun setJudoId(id: String?) = apply { this.judoId = id }

        /**
         * Sets the [Authorization] type, can be [com.judopay.judokit.android.api.model.BasicAuthorization]
         * or [com.judopay.judokit.android.api.model.PaymentSessionAuthorization]
         * @param authorization Type of authorization
         * @see com.judopay.judokit.android.api.model.BasicAuthorization
         * @see com.judopay.judokit.android.api.model.PaymentSessionAuthorization
         **/
        fun setAuthorization(authorization: Authorization?) =
            apply { this.authorization = authorization }

        /**
         * Sets environment type.
         * @param sandboxed A property that toggles sandbox mode on the Judo SDK.
         */
        fun setIsSandboxed(sandboxed: Boolean?) = apply { this.isSandboxed = sandboxed }

        /**
         * Sets the amount and currency of a payment transaction
         * @param amount Information about amount and the corresponding currency for a transaction
         * @see Amount
         */
        fun setAmount(amount: Amount?) = apply { this.amount = amount }

        /**
         *  Sets reference object.
         *  @param reference The Reference object is supposed to simplify storing reference data like consumer,
         *  payment references and metadata dictionary that can hold an arbitrary
         *  set of key value based information
         *  @see Reference
         */
        fun setReference(reference: Reference?) = apply { this.reference = reference }

        /**
         * Sets Ui customization options.
         * @param configuration A set of configuration values that changes the UI
         * @see UiConfiguration
         */
        fun setUiConfiguration(configuration: UiConfiguration?) =
            apply { this.uiConfiguration = configuration }

        /**
         * Sets an array of payment methods to be supported in payment methods screen.
         * @param methods Array of supported payment methods to display in payment methods screen
         * @see PaymentMethod
         */
        fun setPaymentMethods(methods: Array<PaymentMethod>?) =
            apply { this.paymentMethods = methods }

        /**
         * Sets an array of card networks to be supported.
         * @param networks Array of supported card networks
         * @see CardNetwork
         */
        fun setSupportedCardNetworks(networks: Array<CardNetwork>?) =
            apply { this.supportedCardNetworks = networks }

        /**
         * Sets primary account details information.
         * @param details Object that contains information about the account details provided by the merchant
         * @see PrimaryAccountDetails
         */
        fun setPrimaryAccountDetails(details: PrimaryAccountDetails?) =
            apply { this.primaryAccountDetails = details }

        /**
         * Sets GooglePay configuration.
         * @param configuration A configuration class responsible for setting all the necessary
         * parameters for a GooglePay transaction
         * @see GooglePayConfiguration
         */
        fun setGooglePayConfiguration(configuration: GooglePayConfiguration?) =
            apply { this.googlePayConfiguration = configuration }

        /**
         * Sets address configuration to be sent as part of payment requests.
         * @param address An object containing information about the card address.
         * If set, will be sent to the Judo backend as part of the card details
         * @see Address
         */
        fun setAddress(address: Address?) =
            apply { this.address = address }

        /**
         * Sets the flag for initial recurring payment.
         * @param initialRecurringPayment Boolean value that toggles initial recurring payment.
         */
        fun setInitialRecurringPayment(initialRecurringPayment: Boolean?) =
            apply { this.initialRecurringPayment = initialRecurringPayment }

        /**
         * Sets the flag for initial delayed authorization.
         * @param delayedAuthorisation Boolean value that toggles delayed authorization payment.
         */
        fun setDelayedAuthorisation(delayedAuthorisation: Boolean?) =
            apply { this.delayedAuthorisation = delayedAuthorisation }

        /**
         * Sets the network timeout.
         * @param networkTimeout class for setting read, write and connect timeouts.
         */
        fun setNetworkTimeout(networkTimeout: NetworkTimeout?) =
            apply { this.networkTimeout = networkTimeout }

        /**
         * Sets the value for challenge request indicator.
         * @param challengeRequestIndicator Enum value [ChallengeRequestIndicator].
         */
        fun setChallengeRequestIndicator(challengeRequestIndicator: ChallengeRequestIndicator?) =
            apply { this.challengeRequestIndicator = challengeRequestIndicator }

        /**
         * Sets the value for SCA exemption.
         * @param scaExemption Enum value [ScaExemption].
         */
        fun setScaExemption(scaExemption: ScaExemption?) =
            apply { this.scaExemption = scaExemption }

        /**
         * Sets the cardholder's mobile number.
         * @param mobileNumber the mobile number of the cardholder.
         */
        fun setMobileNumber(mobileNumber: String?) =
            apply { this.mobileNumber = mobileNumber }

        /**
         * Sets the cardholder's email address.
         * @param emailAddress the email address of the cardholder.
         */
        fun setEmailAddress(emailAddress: String?) =
            apply { this.emailAddress = emailAddress }

        /**
         * Sets the maximum timeout for 3DS 2.0 transactions.
         * @param maxTimeout max timeout in minutes.
         */
        fun setThreeDSTwoMaxTimeout(maxTimeout: Int?) =
            apply { this.threeDSTwoMaxTimeout = maxTimeout }

        /**
         * Sets the protocol message version for 3DS 2.0 transactions.
         * @param messageVersion 3DS 2.0 protocol message version.
         */
        fun setThreeDSTwoMessageVersion(messageVersion: String?) =
            apply { this.threeDSTwoMessageVersion = messageVersion }

        /**
         * Sets phone country code.
         */
        fun setPhoneCountryCode(phoneCountryCode: String?) =
            apply { this.phoneCountryCode = phoneCountryCode }

        /*
         * Sets card token details for token payment.
         */
        fun setCardToken(cardToken: CardToken?) =
            apply { this.cardToken = cardToken }

        /*
         * Sets the card security code for token payment.
         */
        fun setCardSecurityCode(cardSecurityCode: String?) =
            apply { this.cardSecurityCode = cardSecurityCode }

        fun setSubProductInfo(subProductInfo: SubProductInfo) =
            apply { this.subProductInfo = subProductInfo }

        /**
         * Method that initializes Judo configuration object that can be used for
         * processing a payment.
         * @return A new Judo object that can be added to an intent with [JUDO_OPTIONS] key
         * and start a payment flow.
         * @throws IllegalArgumentException If any of the required fields are empty/null or invalid
         */
        @Throws(IllegalArgumentException::class)
        fun build(): Judo {
            val id = requireJudoId(
                judoId,
                "The Judo ID parameter cannot be null or empty. The required Judo ID parameter has not been set in the Judo configuration."
            )
            val myAuthorization = requireNotNull(authorization, "authorization")

            val myAmount = requireNotNull(amount, "amount")
            val myReference = requireNotNull(reference, "reference")

            validatePaymentMethods(myAmount.currency)

            val myUiConfiguration = requireNotNull(uiConfiguration, "uiConfiguration")
            val mySandboxed = isSandboxed ?: false

            val defaultPaymentMethods = arrayOf(PaymentMethod.CARD)

            val defaultSupportedCardNetworks = arrayOf(
                CardNetwork.VISA,
                CardNetwork.MASTERCARD,
                CardNetwork.AMEX,
                CardNetwork.MAESTRO
            )

            val myPaymentMethods = if (paymentMethods.isNullOrEmpty()) {
                defaultPaymentMethods
            } else {
                checkNotNull(paymentMethods)
            }

            val mySupportedCardNetworks = if (supportedCardNetworks.isNullOrEmpty()) {
                defaultSupportedCardNetworks
            } else {
                checkNotNull(supportedCardNetworks)
            }

            val myNetworkTimeout = networkTimeout ?: NetworkTimeout.Builder().build()

            return Judo(
                judoId = id,
                authorization = myAuthorization,
                isSandboxed = mySandboxed,
                amount = myAmount,
                reference = myReference,
                uiConfiguration = myUiConfiguration,
                paymentMethods = myPaymentMethods,
                supportedCardNetworks = mySupportedCardNetworks,
                primaryAccountDetails = primaryAccountDetails,
                googlePayConfiguration = googlePayConfiguration,
                paymentWidgetType = paymentWidgetType,
                address = address,
                initialRecurringPayment = initialRecurringPayment,
                networkTimeout = myNetworkTimeout,
                challengeRequestIndicator = challengeRequestIndicator,
                scaExemption = scaExemption,
                mobileNumber = mobileNumber,
                phoneCountryCode = phoneCountryCode,
                emailAddress = emailAddress,
                threeDSTwoMaxTimeout = threeDSTwoMaxTimeout ?: DEFAULT_MAX_TIMEOUT,
                threeDSTwoMessageVersion = threeDSTwoMessageVersion ?: THREE_DS_TWO_MESSAGE_VERSION_TWO_DOT_TWO,
                delayedAuthorisation = delayedAuthorisation,
                cardToken = cardToken,
                cardSecurityCode = cardSecurityCode,
                subProductInfo = subProductInfo
            )
        }

        @Throws(IllegalArgumentException::class)
        private fun validatePaymentMethods(currency: Currency) = paymentMethods?.let { methods ->
            // Payment methods accepted currencies checks
            if (methods.size == 1) {
                val method = methods.first()
                val expectedCurrency = when (method) {
                    PaymentMethod.IDEAL -> Currency.EUR
                    else -> null
                }
                if (expectedCurrency != null && currency != expectedCurrency) {
                    throw IllegalArgumentException("${method.name} transactions only support ${expectedCurrency.name} as the currency. Invalid currency passed to ${method.name} transaction configuration.")
                }
            }
        }

        @Throws(IllegalArgumentException::class)
        private fun requireJudoId(judoId: String?, message: String?): String {
            val id = requireNotNullOrEmpty(judoId, "judoId", message)
            if (id.matches(REGEX_JUDO_ID.toRegex())) {
                return id
            } else {
                throw IllegalArgumentException("The Judo ID entered is invalid. The specified Judo ID parameter has an incorrect format.")
            }
        }
    }

    override fun toString(): String {
        return "Judo(judoId='$judoId', authorization=$authorization, isSandboxed=$isSandboxed, amount=$amount, reference=$reference, uiConfiguration=$uiConfiguration, paymentMethods=${paymentMethods.contentToString()}, supportedCardNetworks=${supportedCardNetworks.contentToString()}, primaryAccountDetails=$primaryAccountDetails, googlePayConfiguration=$googlePayConfiguration, paymentWidgetType=$paymentWidgetType, address=$address, initialRecurringPayment=$initialRecurringPayment, challengeRequestIndicator=$challengeRequestIndicator, scaExemption=$scaExemption, delayedAuthorisation=$delayedAuthorisation, cardToken=$cardToken)"
    }
}
