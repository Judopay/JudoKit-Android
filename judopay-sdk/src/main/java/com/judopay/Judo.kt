package com.judopay

import android.app.Activity
import android.os.Parcelable
import com.judopay.api.model.request.Address
import com.judopay.model.Amount
import com.judopay.model.CardNetwork
import com.judopay.model.Currency
import com.judopay.model.GooglePayConfiguration
import com.judopay.model.PaymentMethod
import com.judopay.model.PaymentWidgetType
import com.judopay.model.PrimaryAccountDetails
import com.judopay.model.Reference
import com.judopay.model.UiConfiguration
import kotlinx.android.parcel.Parcelize

/**
 * The wrapper for providing data to Activity and Fragments classes in the SDK (e.g. JudopayActivity).
 * <p>
 * Use the {@link Judo.Builder class for constructing} an instance of {@link Judo}.
 * When calling an Activity with an Intent extra or a Fragment using an arguments Bundle,
 * use {@link Judo#JUDO_OPTIONS} as the extra or argument name.
 */

// Bundle keys
const val JUDO_OPTIONS = "com.judopay.options"
const val JUDO_RESULT = "com.judopay.result"
const val JUDO_ERROR = "com.judopay.error"

// Result codes
/** Judo activity result: operation succeeded.  */
const val PAYMENT_SUCCESS = Activity.RESULT_FIRST_USER + 1

/** Judo activity result: operation canceled.  */
const val PAYMENT_CANCELLED = Activity.RESULT_FIRST_USER + 2

/** Judo activity result: operation error  */
const val PAYMENT_ERROR = Activity.RESULT_FIRST_USER + 3
@Parcelize
class Judo internal constructor(
    val judoId: String,
    val siteId: String?,
    val apiToken: String,
    val apiSecret: String,
    val isSandboxed: Boolean,
    val amount: Amount,
    val reference: Reference,
    val uiConfiguration: UiConfiguration,
    val paymentMethods: Array<PaymentMethod>,
    val supportedCardNetworks: Array<CardNetwork>,
    val primaryAccountDetails: PrimaryAccountDetails?,
    val googlePayConfiguration: GooglePayConfiguration?,
    val paymentWidgetType: PaymentWidgetType,
    val address: Address?
) : Parcelable {

    class Builder(private val paymentWidgetType: PaymentWidgetType) {
        private var judoId: String? = null
        private var siteId: String? = null
        private var apiToken: String? = null
        private var apiSecret: String? = null
        private var isSandboxed: Boolean? = null
        private var amount: Amount? = null
        private var reference: Reference? = null
        private var uiConfiguration: UiConfiguration? = null
        private var paymentMethods: Array<PaymentMethod>? = null
        private var supportedCardNetworks: Array<CardNetwork>? = null
        private var primaryAccountDetails: PrimaryAccountDetails? = null
        private var googlePayConfiguration: GooglePayConfiguration? = null
        private var address: Address? = null

        fun setJudoId(id: String?) = apply { this.judoId = id }
        fun setSiteId(id: String?) = apply { this.siteId = id }
        fun setApiToken(token: String?) = apply { this.apiToken = token }
        fun setApiSecret(secret: String?) = apply { this.apiSecret = secret }
        fun setIsSandboxed(sandboxed: Boolean?) = apply { this.isSandboxed = sandboxed }
        fun setAmount(amount: Amount?) = apply { this.amount = amount }
        fun setReference(reference: Reference?) = apply { this.reference = reference }
        fun setUiConfiguration(configuration: UiConfiguration?) =
            apply { this.uiConfiguration = configuration }

        fun setPaymentMethods(methods: Array<PaymentMethod>?) =
            apply { this.paymentMethods = methods }

        fun setSupportedCardNetworks(networks: Array<CardNetwork>?) =
            apply { this.supportedCardNetworks = networks }

        fun setPrimaryAccountDetails(details: PrimaryAccountDetails?) =
            apply { this.primaryAccountDetails = details }

        fun setGooglePayConfiguration(configuration: GooglePayConfiguration?) =
            apply { this.googlePayConfiguration = configuration }

        fun setAddress(address: Address?) =
            apply { this.address = address }

        @Throws(IllegalArgumentException::class)
        fun build(): Judo {
            val id = requireNotNullOrEmpty(judoId, "judoId")
            val token = requireNotNullOrEmpty(apiToken, "apiToken")
            val secret = requireNotNullOrEmpty(apiSecret, "apiSecret")

            val myAmount = isAmountRequired(paymentWidgetType, amount)
            val myReference = requireNotNull(reference, "reference")

            paymentMethods?.let {
                if (it.size == 1 && it.first() == PaymentMethod.IDEAL && myAmount.currency != Currency.EUR) {
                    throw IllegalArgumentException("Cannot make iDEAL transactions with currencies different than EUR")
                }
            }

            val myUiConfiguration = uiConfiguration
                ?: UiConfiguration.Builder()
                    .setAvsEnabled(false)
                    .setShouldDisplayAmount(true)
                    .build()
            val mySandboxed = isSandboxed ?: false

            val defaultPaymentMethods = arrayOf(PaymentMethod.CARD)
            val defaultSupportedCardNetworks = arrayOf(
                CardNetwork.VISA,
                CardNetwork.MASTERCARD,
                CardNetwork.AMEX,
                CardNetwork.MAESTRO
            )

            val myPaymentMethods =
                if (paymentMethods.isNullOrEmpty()) defaultPaymentMethods else checkNotNull(
                    paymentMethods
                )
            val mySupportedCardNetworks =
                if (supportedCardNetworks.isNullOrEmpty()) defaultSupportedCardNetworks else checkNotNull(
                    supportedCardNetworks
                )

            return Judo(
                id,
                siteId,
                token,
                secret,
                mySandboxed,
                myAmount,
                myReference,
                myUiConfiguration,
                myPaymentMethods,
                mySupportedCardNetworks,
                primaryAccountDetails,
                googlePayConfiguration,
                paymentWidgetType,
                address
            )
        }
    }

    override fun toString(): String {
        return "Judo(judoId='$judoId', siteId=$siteId, apiToken='$apiToken', apiSecret='$apiSecret', isSandboxed=$isSandboxed, amount=$amount, reference=$reference, uiConfiguration=$uiConfiguration, paymentMethods=${paymentMethods.contentToString()}, supportedCardNetworks=${supportedCardNetworks.contentToString()}, primaryAccountDetails=$primaryAccountDetails, googlePayConfiguration=$googlePayConfiguration, paymentWidgetType=$paymentWidgetType, address=$address)"
    }
}
