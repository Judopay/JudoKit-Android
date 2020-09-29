package com.judokit.android.api.model.request

import com.judokit.android.model.Currency
import com.judokit.android.requireNotNullOrEmpty

data class IdealSaleRequest(
    val amount: String,
    val merchantPaymentReference: String,
    val paymentMetadata: Map<String, String>?,
    val merchantConsumerReference: String,
    val judoId: String,
    val bic: String,
    val currency: String = Currency.EUR.name,
    val country: String = "NL",
    val paymentMethod: String = "IDEAL",
    val accountHolderName: String = "iDEAL User"
) {
    class Builder {
        private var amount: String? = null
        private var merchantPaymentReference: String? = null
        private var paymentMetadata: Map<String, String>? = null
        private var merchantConsumerReference: String? = null
        private var judoId: String? = null
        private var bic: String? = null

        fun setAmount(amount: String?) = apply { this.amount = amount }

        fun setMerchantPaymentReference(merchantPaymentReference: String?) =
            apply { this.merchantPaymentReference = merchantPaymentReference }

        fun setPaymentMetadata(paymentMetadata: Map<String, String>?) =
            apply { this.paymentMetadata = paymentMetadata }

        fun setMerchantConsumerReference(merchantConsumerReference: String?) =
            apply { this.merchantConsumerReference = merchantConsumerReference }

        fun setJudoId(judoId: String?) = apply { this.judoId = judoId }

        fun setBic(bic: String?) = apply { this.bic = bic }

        fun build(): IdealSaleRequest {
            val myAmount = requireNotNullOrEmpty(amount, "amount")
            val myMerchantPaymentReference =
                requireNotNullOrEmpty(merchantPaymentReference, "merchantPaymentReference")
            val myMerchantConsumerReference =
                requireNotNullOrEmpty(merchantConsumerReference, "merchantConsumerReference")
            val myJudoId = requireNotNullOrEmpty(judoId, "judoId")
            val myBic = requireNotNullOrEmpty(bic, "bic")

            return IdealSaleRequest(
                myAmount,
                myMerchantPaymentReference,
                paymentMetadata,
                myMerchantConsumerReference,
                myJudoId,
                myBic
            )
        }
    }
}
