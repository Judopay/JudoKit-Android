package com.judopay.judokit.android.api.model.response

import com.google.gson.JsonObject
import com.judopay.judo3ds2.transaction.challenge.ChallengeParameters
import com.judopay.judokit.android.model.CardVerificationModel
import com.judopay.judokit.android.model.JudoResult
import java.math.BigDecimal
import java.util.Date

private const val CHALLENGE_REQUIRED_MESSAGE = "Issuer ACS has responded with a Challenge URL"

/**
 * The Receipt of a transaction performed with the judo API.
 */
class Receipt(
    var judoId: Long? = null,
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
    val result: String? = null,
    val message: String? = null,
    val acsReferenceNumber: String? = null,
    val acsSignedContent: String? = null,
    val acsRenderingType: JsonObject? = null,
    val acsInterface: AcsInterface? = null,
    val acsUiTemplate: String? = null,
    val threeDSServerTransactionID: String? = null,
    val acsTransactionId: String? = null,
    val acsThreeDSRequestorAppURL: String? = null
) {

    val is3dSecureRequired: Boolean
        get() = !(acsUrl.isNullOrEmpty() && md.isNullOrEmpty() && paReq.isNullOrEmpty())

    val is3dSecure2Required: Boolean
        get() = message == CHALLENGE_REQUIRED_MESSAGE

    override fun toString(): String {
        return "Receipt(judoId=$judoId, receiptId=$receiptId, originalReceiptId=$originalReceiptId, partnerServiceFee=$partnerServiceFee, yourPaymentReference=$yourPaymentReference, type=$type, createdAt=$createdAt, merchantName=$merchantName, appearsOnStatementAs=$appearsOnStatementAs, originalAmount=$originalAmount, netAmount=$netAmount, amount=$amount, currency=$currency, cardDetails=$cardDetails, consumer=$consumer, risks=$risks, md=$md, paReq=$paReq, acsUrl=$acsUrl, result=$result, message=$message)"
    }
}

fun Receipt.toJudoResult() = JudoResult(
    judoId.toString(),
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
    result,
    message
)

fun Receipt.toCardVerificationModel() = CardVerificationModel.Builder()
    .setReceiptId(receiptId)
    .setMd(md)
    .setPaReq(paReq)
    .setAcsUrl(acsUrl)
    .build()

fun Receipt.toChallengeParameters() = ChallengeParameters(
    threeDSServerTransactionID,
    acsTransactionId,
    acsReferenceNumber,
    acsSignedContent,
    acsThreeDSRequestorAppURL
)
