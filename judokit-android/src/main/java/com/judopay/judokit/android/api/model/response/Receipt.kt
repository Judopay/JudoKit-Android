package com.judopay.judokit.android.api.model.response

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.judopay.judo3ds2.transaction.challenge.ChallengeParameters
import com.judopay.judokit.android.model.CardVerificationModel
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.trimIndent
import java.math.BigDecimal
import java.util.Date

private const val DECLINED_RESULT = "Declined"
private const val SOFT_DECLINED_MESSAGE = "Card declined: Additional customer authentication required"
private const val CHALLENGE_REQUIRED_MESSAGE = "Issuer ACS has responded with a Challenge URL"

/**
 * The Receipt of a transaction performed with the judo API.
 */
@Suppress("LongParameterList")
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
    var yourPaymentMetaData: Map<String, String>? = null,
    val acsReferenceNumber: String? = null,
    val acsSignedContent: String? = null,
    val acsRenderingType: JsonObject? = null,
    val acsInterface: AcsInterface? = null,
    val acsUiTemplate: String? = null,
    val threeDSServerTransactionID: String? = null,
    val acsTransactionId: String? = null,
    val acsThreeDSRequestorAppURL: String? = null,
    val cReq: String? = null,
    val emailAddress: String? = null,
) {
    val isThreeDSecureTwoRequired: Boolean
        get() = message.equals(CHALLENGE_REQUIRED_MESSAGE, true)

    val isSoftDeclined: Boolean
        get() = result.equals(DECLINED_RESULT, true) && message.equals(SOFT_DECLINED_MESSAGE, true) && !receiptId.isNullOrEmpty()

    override fun toString(): String =
        """
            Receipt(
                judoId=$judoId,
                receiptId=$receiptId,
                originalReceiptId=$originalReceiptId,
                partnerServiceFee=$partnerServiceFee,
                yourPaymentReference=$yourPaymentReference,
                type=$type,
                createdAt=$createdAt,
                merchantName=$merchantName,
                appearsOnStatementAs=$appearsOnStatementAs,
                originalAmount=$originalAmount,
                netAmount=$netAmount,
                amount=$amount,
                currency=$currency,
                cardDetails=$cardDetails,
                consumer=$consumer,
                risks=$risks,
                md=$md,
                paReq=$paReq,
                acsUrl=$acsUrl,
                result=$result,
                message=$message,
                emailAddress=$emailAddress
            )
            """.trimIndent(true)
}

fun Receipt.toJudoResult() =
    JudoResult(
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
        message,
        yourPaymentMetaData,
        emailAddress,
    )

fun Receipt.toCardVerificationModel() =
    CardVerificationModel
        .Builder()
        .setReceiptId(receiptId)
        .setMd(md)
        .setPaReq(paReq)
        .setAcsUrl(acsUrl)
        .build()

data class CReqParameters(
    val messageType: String,
    val messageVersion: String,
    val threeDSServerTransID: String,
    val acsTransID: String,
)

fun Receipt.getCReqParameters(): CReqParameters? =
    try {
        Gson().fromJson(
            String(Base64.decode(cReq, Base64.NO_WRAP)),
            CReqParameters::class.java,
        )
    } catch (exception: JsonSyntaxException) {
        Log.e("getCReqParameters", exception.toString())
        null
    }

fun Receipt.getChallengeParameters(cReqParams: CReqParameters? = getCReqParameters()): ChallengeParameters =
    ChallengeParameters(
        cReqParams?.threeDSServerTransID,
        cReqParams?.acsTransID,
        acsReferenceNumber,
        acsSignedContent,
        null,
        cReqParams?.messageVersion,
    )
