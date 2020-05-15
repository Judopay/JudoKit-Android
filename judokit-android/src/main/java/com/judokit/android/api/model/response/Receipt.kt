package com.judokit.android.api.model.response

import com.judokit.android.model.CardVerificationModel
import com.judokit.android.model.JudoResult
import java.math.BigDecimal
import java.util.Date

/**
 * The Receipt of a transaction performed with the judo API.
 */
class Receipt(
    var judoID: Long? = null,
    var receiptId: String? = null,
    var originalReceiptId: String? = null,
    var partnerServiceFee: String? = null,
    var yourPaymentReference: String? = null,
    var type: String? = null,
    var createdAt: Date? = null,
    var merchantName: String? = null,
    var appearsOnStatementAs: String? = null,
    var originalAmount: BigDecimal? = null,
    var netAmount: BigDecimal? = null,
    var amount: BigDecimal? = null,
    var currency: String? = null,
    var cardDetails: CardToken? = null,
    var consumer: Consumer? = null,
    var risks: Risks? = null,
    var md: String? = null,
    var paReq: String? = null,
    var acsUrl: String? = null,
    val result: String? = null
) {

    val is3dSecureRequired: Boolean
        get() = !(acsUrl.isNullOrEmpty() && md.isNullOrEmpty() && paReq.isNullOrEmpty())

    override fun toString(): String {
        return "Receipt(judoID=$judoID, receiptId=$receiptId, originalReceiptId=$originalReceiptId, partnerServiceFee=$partnerServiceFee, yourPaymentReference=$yourPaymentReference, type=$type, createdAt=$createdAt, merchantName=$merchantName, appearsOnStatementAs=$appearsOnStatementAs, originalAmount=$originalAmount, netAmount=$netAmount, amount=$amount, currency=$currency, cardDetails=$cardDetails, consumer=$consumer, risks=$risks, md=$md, paReq=$paReq, acsUrl=$acsUrl, result=$result)"
    }
}

fun Receipt.toJudoResult() = JudoResult(
    receiptId,
    originalReceiptId,
    partnerServiceFee,
    yourPaymentReference,
    type,
    createdAt,
    merchantName,
    appearsOnStatementAs,
    originalAmount,
    netAmount,
    amount,
    currency,
    cardDetails,
    consumer,
    result
)

fun Receipt.toCardVerificationModel() = CardVerificationModel.Builder()
    .setReceiptId(receiptId)
    .setMd(md)
    .setPaReq(paReq)
    .setAcsUrl(acsUrl)
    .build()
