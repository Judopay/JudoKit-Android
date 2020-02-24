package com.judopay

import android.os.Parcelable
import com.judopay.model.*
import kotlinx.android.parcel.Parcelize
import java.io.IOException

/**
 * The wrapper for providing data to Activity and Fragments classes in the SDK (e.g. JudopayActivity).
 * <p>
 * Use the {@link Judo.Builder class for constructing} an instance of {@link Judo}.
 * When calling an Activity with an Intent extra or a Fragment using an arguments Bundle,
 * use {@link Judo#JUDO_OPTIONS} as the extra or argument name.
 */

// Bundle keys
const val JUDO_OPTIONS = "com.judopay.judo-options"

// Request codes
const val PAYMENT_METHODS = 1

// Response codes


@Parcelize
class Judo internal constructor(val judoId: String,
                                val siteId: String?,
                                val apiToken: String,
                                val apiSecret: String,
                                val isSandboxed: Boolean,
                                val amount: Amount,
                                val reference: Reference,
                                val uiConfiguration: UiConfiguration,
                                val paymentMethods: Array<PaymentMethods>,
                                val supportedCardNetworks: Array<CardNetwork>,
                                val primaryAccountDetails: PrimaryAccountDetails?,
                                val googlePayConfiguration: GooglePayConfiguration?
) : Parcelable {

    class Builder {
        private var judoId: String? = null
        private var siteId: String? = null
        private var apiToken: String? = null
        private var apiSecret: String? = null
        private var isSandboxed: Boolean? = null
        private var amount: Amount? = null
        private var reference: Reference? = null
        private var uiConfiguration: UiConfiguration? = null
        private var paymentMethods: Array<PaymentMethods>? = null
        private var supportedCardNetworks: Array<CardNetwork>? = null
        private var primaryAccountDetails: PrimaryAccountDetails? = null
        private var googlePayConfiguration: GooglePayConfiguration? = null

        fun setJudoId(id: String?) = apply { this.judoId = id }
        fun setSiteId(id: String?) = apply { this.siteId = id }
        fun setApiToken(token: String?) = apply { this.apiToken = token }
        fun setApiSecret(secret: String?) = apply { this.apiSecret = secret }
        fun setIsSandboxed(sandboxed: Boolean?) = apply { this.isSandboxed = sandboxed }
        fun setAmount(amount: Amount?) = apply { this.amount = amount }
        fun setReference(reference: Reference?) = apply { this.reference = reference }
        fun setUiConfiguration(configuration: UiConfiguration?) = apply { this.uiConfiguration = configuration }
        fun setPaymentMethods(methods: Array<PaymentMethods>?) = apply { this.paymentMethods = methods }
        fun setSupportedCardNetworks(networks: Array<CardNetwork>?) = apply { this.supportedCardNetworks = networks }
        fun setPrimaryAccountDetails(details: PrimaryAccountDetails?) = apply { this.primaryAccountDetails = details }
        fun setGooglePayConfiguration(configuration: GooglePayConfiguration?) = apply { this.googlePayConfiguration = configuration }

        @Throws(IllegalArgumentException::class)
        fun build(): Judo {
            val id = requireNotNullOrEmpty(judoId, "judoId")
            val token = requireNotNullOrEmpty(apiToken, "apiToken")
            val secret = requireNotNullOrEmpty(apiSecret, "apiSecret")
            val myAmount = requireNotNull(amount, "amount")
            val myReference = requireNotNull(reference, "reference")

            val myUiConfiguration = uiConfiguration
                    ?: UiConfiguration.Builder().setAvsEnabled(false).build()
            val mySandboxed = isSandboxed ?: false

            val defaultPaymentMethods = arrayOf(PaymentMethods.CARD)
            val defaultSupportedCardNetworks = arrayOf(CardNetwork.VISA)

            val myPaymentMethods = if (paymentMethods.isNullOrEmpty()) defaultPaymentMethods else checkNotNull(paymentMethods)
            val mySupportedCardNetworks = if (supportedCardNetworks.isNullOrEmpty()) defaultSupportedCardNetworks else checkNotNull(supportedCardNetworks)

            return Judo(id,
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
                    googlePayConfiguration)
        }
    }
}