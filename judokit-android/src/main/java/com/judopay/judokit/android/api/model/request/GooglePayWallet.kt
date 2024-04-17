package com.judopay.judokit.android.api.model.request

import com.judopay.judokit.android.model.googlepay.GooglePayAddress
import com.judopay.judokit.android.model.googlepay.GooglePayPaymentData
import com.judopay.judokit.android.requireNotNullOrEmpty

class GooglePayWallet private constructor(
    internal val cardNetwork: String,
    internal val cardDetails: String,
    internal val token: String,
    internal val billingAddress: GooglePayAddress?,
) {
    class Builder {
        private var cardNetwork: String? = null
        private var cardDetails: String? = null
        private var token: String? = null
        private var billingAddress: GooglePayAddress? = null
        private var googlePayPaymentData: GooglePayPaymentData? = null

        fun setCardNetwork(network: String?) = apply { this.cardNetwork = network }

        fun setCardDetails(details: String?) = apply { this.cardDetails = details }

        fun setToken(token: String?) = apply { this.token = token }

        fun setBillingAddress(billingAddress: GooglePayAddress?) = apply { this.billingAddress = billingAddress }

        fun setGooglePayPaymentData(data: GooglePayPaymentData) = apply { this.googlePayPaymentData = data }

        fun build(): GooglePayWallet {
            googlePayPaymentData?.let {
                val paymentMethodData = it.paymentMethodData
                val info = paymentMethodData.info
                val tokenizationData = paymentMethodData.tokenizationData

                val myCardNetwork = requireNotNullOrEmpty(info.cardNetwork, "cardNetwork")
                val myCardDetails = requireNotNullOrEmpty(info.cardDetails, "cardDetails")
                val myToken = requireNotNullOrEmpty(tokenizationData.token, "token")

                return GooglePayWallet(myCardNetwork, myCardDetails, myToken, info.billingAddress)
            }

            val myCardNetwork = requireNotNullOrEmpty(cardNetwork, "cardNetwork")
            val myCardDetails = requireNotNullOrEmpty(cardDetails, "cardDetails")
            val myToken = requireNotNullOrEmpty(token, "token")
            return GooglePayWallet(myCardNetwork, myCardDetails, myToken, billingAddress)
        }
    }
}
