package com.judokit.android.api.model.request

import com.judokit.android.requireNotNullOrEmpty

class GooglePayWallet private constructor(
    internal val cardNetwork: String,
    private val cardDetails: String,
    internal val token: String
) {

    class Builder {
        private var cardNetwork: String? = null
        private var cardDetails: String? = null
        private var token: String? = null

        fun setCardNetwork(network: String?) = apply { this.cardNetwork = network }

        fun setCardDetails(details: String?) = apply { this.cardDetails = details }

        fun setToken(token: String?) = apply { this.token = token }

        fun build(): GooglePayWallet {
            val myCardNetwork = requireNotNullOrEmpty(cardNetwork, "cardNetwork")
            val myCardDetails = requireNotNullOrEmpty(cardDetails, "cardDetails")
            val myToken = requireNotNullOrEmpty(token, "token")
            return GooglePayWallet(myCardNetwork, myCardDetails, myToken)
        }
    }
}
