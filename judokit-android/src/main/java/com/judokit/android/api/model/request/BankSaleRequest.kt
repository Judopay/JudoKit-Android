package com.judokit.android.api.model.request

import com.judokit.android.model.Country
import com.judokit.android.model.Currency
import com.judokit.android.requireNotNull
import com.judokit.android.requireNotNullOrEmpty
import java.math.BigDecimal

data class BankSaleRequest(
    val amount: BigDecimal,
    val merchantPaymentReference: String,
    val merchantConsumerReference: String,
    val siteId: String,
    val mobileNumber: String?,
    val emailAddress: String?,
    val appearsOnStatement: String?,
    val paymentMetadata: Map<String, String>?,
    val accountHolderName: String = "PBBA User",
    val bic: String = "RABONL2U",
    val paymentMethod: String = "PBBA",
    val country: String = Country.GB.name,
    val currency: String = Currency.GBP.name,
    val merchantRedirectUrl: String = "fb://messaging/"
) {
    class Builder {
        private var amount: BigDecimal? = null
        private var merchantPaymentReference: String? = null
        private var merchantConsumerReference: String? = null
        private var siteId: String? = null
        private var mobileNumber: String? = null
        private var appearsOnStatement: String? = null
        private var emailAddress: String? = null
        private var paymentMetadata: Map<String, String>? = null

        fun setAmount(amount: BigDecimal?) = apply { this.amount = amount }

        fun setMerchantPaymentReference(merchantPaymentReference: String?) =
            apply { this.merchantPaymentReference = merchantPaymentReference }

        fun setMerchantConsumerReference(merchantConsumerReference: String?) =
            apply { this.merchantConsumerReference = merchantConsumerReference }

        fun setSiteId(siteId: String?) = apply { this.siteId = siteId }

        fun setMobileNumber(mobileNumber: String?) = apply { this.mobileNumber = mobileNumber }
        fun setAppearsOnStatement(appearsOnStatement: String?) =
            apply { this.appearsOnStatement = appearsOnStatement }

        fun setEmailAddress(emailAddress: String?) = apply { this.emailAddress = emailAddress }
        fun setPaymentMetadata(paymentMetadata: Map<String, String>?) =
            apply { this.paymentMetadata = paymentMetadata }

        fun build(): BankSaleRequest {
            val myAmount = requireNotNull(amount, "amount")
            val myMerchantPaymentReference =
                requireNotNullOrEmpty(merchantPaymentReference, "merchantPaymentReference")
            val myMerchantConsumerReference =
                requireNotNullOrEmpty(merchantConsumerReference, "merchantConsumerReference")
            val mySiteId = requireNotNullOrEmpty(siteId, "siteId")

            return BankSaleRequest(
                myAmount,
                myMerchantPaymentReference,
                myMerchantConsumerReference,
                mySiteId,
                mobileNumber,
                emailAddress,
                appearsOnStatement,
                paymentMetadata
            )
        }
    }
}
