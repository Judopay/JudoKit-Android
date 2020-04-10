package com.judopay.api.model.request

import com.judopay.api.model.response.CardToken
import com.judopay.api.model.response.Consumer
import com.judopay.api.model.response.Receipt
import com.judopay.model.PrimaryAccountDetails
import com.judopay.requireNotNull
import com.judopay.requireNotNullOrEmpty
import java.util.Date

class GooglePayRequest private constructor(
    internal var judoId: String?,
    internal var amount: String?,
    internal var currency: String?,
    internal var yourPaymentReference: String?,
    internal var yourConsumerReference: String?,
    private var yourPaymentMetaData: Map<String, String>?,
    private var primaryAccountDetails: PrimaryAccountDetails?,
    internal val googlePayWallet: GooglePayWallet
) {

    class Builder {
        private var judoId: String? = null
        private var amount: String? = null
        private var currency: String? = null
        private var yourPaymentReference: String? = null
        private var yourConsumerReference: String? = null
        private var yourPaymentMetaData: Map<String, String>? = null
        private var primaryAccountDetails: PrimaryAccountDetails? = null
        private var googlePayWallet: GooglePayWallet? = null

        fun setJudoId(judoId: String?) = apply { this.judoId = judoId }

        fun setAmount(amount: String?) = apply { this.amount = amount }

        fun setCurrency(currency: String?) = apply { this.currency = currency }

        fun setYourPaymentReference(yourPaymentReference: String?) =
            apply { this.yourPaymentReference = yourPaymentReference }

        fun setYourConsumerReference(yourConsumerReference: String?) =
            apply { this.yourConsumerReference = yourConsumerReference }

        fun setYourPaymentMetaData(yourPaymentMetaData: Map<String, String>?) =
            apply { this.yourPaymentMetaData = yourPaymentMetaData }

        fun setPrimaryAccountDetails(primaryAccountDetails: PrimaryAccountDetails?) =
            apply { this.primaryAccountDetails = primaryAccountDetails }

        fun setGooglePayWallet(wallet: GooglePayWallet?) =
            apply { this.googlePayWallet = wallet }

        fun build(): GooglePayRequest {
            val id = requireNotNullOrEmpty(judoId, "judoId")
            val myAmount = requireNotNullOrEmpty(amount, "amount")
            val myCurrency = requireNotNullOrEmpty(currency, "currency")
            val consumerReference =
                requireNotNullOrEmpty(yourConsumerReference, "yourConsumerReference")
            val paymentReference =
                requireNotNullOrEmpty(yourPaymentReference, "yourPaymentReference")
            val myWallet = requireNotNull(googlePayWallet, "googlePayWallet")

            return GooglePayRequest(
                id,
                myAmount,
                myCurrency,
                paymentReference,
                consumerReference,
                yourPaymentMetaData,
                primaryAccountDetails,
                myWallet
            )
        }
    }
}

fun GooglePayRequest.toReceipt() = Receipt(
    judoID = judoId?.toLong(),
    amount = amount?.toBigDecimal(),
    currency = currency,
    yourPaymentReference = yourPaymentReference,
    createdAt = Date(),
    consumer = Consumer(yourConsumerReference = yourConsumerReference),
    cardDetails = CardToken(
        token = googlePayWallet.token,
        scheme = googlePayWallet.cardNetwork
    )
)
