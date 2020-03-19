package com.judopay.api.model.request

import com.judopay.requireNotNullOrEmpty

class GooglePayWallet private constructor(
    private val cardNetwork: String,
    private val cardDetails: String,
    private val token: String
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
