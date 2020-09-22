package com.judokit.android

import android.app.Activity
import android.os.Parcelable
import com.judokit.android.api.model.Authorization
import com.judokit.android.api.model.request.Address
import com.judokit.android.model.Amount
import com.judokit.android.model.CardNetwork
import com.judokit.android.model.Currency
import com.judokit.android.model.GooglePayConfiguration
import com.judokit.android.model.PBBAConfiguration
import com.judokit.android.model.PaymentMethod
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.model.PrimaryAccountDetails
import com.judokit.android.model.Reference
import com.judokit.android.model.UiConfiguration
import com.judokit.android.ui.common.REGEX_JUDO_ID
import kotlinx.android.parcel.Parcelize

/**
 * The wrapper for providing data to Activity and Fragments classes in the SDK (e.g. JudopayActivity).
 * <p>
 * Use the {@link Judo.Builder class for constructing} an instance of {@link Judo}.
 * When calling an Activity with an Intent extra or a Fragment using an arguments Bundle,
 * use {@link Judo#JUDO_OPTIONS} as the extra or argument name.
 */

// Bundle keys
const val JUDO_OPTIONS = "com.judokit.android.options"
const val JUDO_RESULT = "com.judokit.android.result"
const val JUDO_ERROR = "com.judokit.android.error"

// Result codes
/** Judo activity result: operation succeeded.  */
const val PAYMENT_SUCCESS = Activity.RESULT_FIRST_USER + 1

/** Judo activity result: operation canceled.  */
const val PAYMENT_CANCELLED = Activity.RESULT_FIRST_USER + 2

/** Judo activity result: operation error  */
const val PAYMENT_ERROR = Activity.RESULT_FIRST_USER + 3

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
    val pbbaConfiguration: PBBAConfiguration?
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
        private var pbbaConfiguration: PBBAConfiguration? = null

        /**
         * Sets the unique merchant ID
         * @param id Merchant unique identifier provided by Judo
         */
        fun setJudoId(id: String?) = apply { this.judoId = id }

        /**
         * Sets the [Authorization] type, can be [com.judokit.android.api.model.BasicAuthorization]
         * or [com.judokit.android.api.model.PaymentSessionAuthorization]
         * @param authorization Type of authorization
         * @see com.judokit.android.api.model.BasicAuthorization
         * @see com.judokit.android.api.model.PaymentSessionAuthorization
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
         * Sets Pay by Bank app configuration.
         * @param pbbaConfiguration An instance of PBBAConfiguration required for PbBA
         * Pay-related transactions.
         * @see PBBAConfiguration
         */
        fun setPBBAConfiguration(pbbaConfiguration: PBBAConfiguration?) =
            apply { this.pbbaConfiguration = pbbaConfiguration }

        /**
         * Method that initializes Judo configuration object that can be used for
         * processing a payment.
         * @return A new Judo object that can be added to an intent with [JUDO_OPTIONS] key
         * and start a payment flow.
         * @throws IllegalArgumentException If any of the required fields are empty/null or invalid
         */
        @Throws(IllegalArgumentException::class)
        fun build(): Judo {
            val id = requireJudoId(judoId)
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

            return Judo(
                id,
                myAuthorization,
                mySandboxed,
                myAmount,
                myReference,
                myUiConfiguration,
                myPaymentMethods,
                mySupportedCardNetworks,
                primaryAccountDetails,
                googlePayConfiguration,
                paymentWidgetType,
                address,
                pbbaConfiguration
            )
        }

        @Throws(IllegalArgumentException::class)
        private fun validatePaymentMethods(currency: Currency) = paymentMethods?.let { methods ->
            // Payment methods accepted currencies checks
            if (methods.size == 1) {
                val method = methods.first()
                val expectedCurrency = when (method) {
                    PaymentMethod.IDEAL -> Currency.EUR
                    PaymentMethod.PAY_BY_BANK -> Currency.GBP
                    else -> null
                }
                if (expectedCurrency != null && currency != expectedCurrency) {
                    throw IllegalArgumentException("Cannot make ${method.name} transactions with currencies different than ${expectedCurrency.name}")
                }
            }
        }

        @Throws(IllegalArgumentException::class)
        private fun requireJudoId(judoId: String?): String {
            val id = requireNotNullOrEmpty(judoId, "judoId")
            if (id.matches(REGEX_JUDO_ID.toRegex()))
                return id
            else
                throw IllegalArgumentException("JudoId is invalid")
        }
    }

    override fun toString(): String {
        return "Judo(judoId='$judoId', authorization=$authorization, isSandboxed=$isSandboxed, amount=$amount, reference=$reference, uiConfiguration=$uiConfiguration, paymentMethods=${paymentMethods.contentToString()}, supportedCardNetworks=${supportedCardNetworks.contentToString()}, primaryAccountDetails=$primaryAccountDetails, googlePayConfiguration=$googlePayConfiguration, paymentWidgetType=$paymentWidgetType, address=$address, pbbaConfiguration=$pbbaConfiguration)"
    }
}
