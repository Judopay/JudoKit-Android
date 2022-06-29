package com.judopay.judokit.android.api.model.response

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Date

@DisplayName("Testing Receipt model")
internal class ReceiptTest {

    @DisplayName("Given is3dSecureRequired is called, when acsUrl is null, return false")
    @Test
    fun returnFalseOnIs3dSecureRequiredWithAcsUrlNull() {
        assertFalse(Receipt().isThreeDSecureOneRequired)
    }

    @DisplayName("Given is3dSecureRequired is called, when acsUrl is empty, return false")
    @Test
    fun returnFalseOnIs3dSecureRequiredWithAcsUrlEmpty() {
        assertFalse(Receipt(acsUrl = "").isThreeDSecureOneRequired)
    }

    @DisplayName("Given is3dSecureRequired is called, when md is null, return false")
    @Test
    fun returnFalseOnIs3dSecureRequiredWithMdNull() {
        assertFalse(Receipt().isThreeDSecureOneRequired)
    }

    @DisplayName("Given is3dSecureRequired is called, when md is empty, return false")
    @Test
    fun returnFalseOnIs3dSecureRequiredWithMdEmpty() {
        assertFalse(Receipt(md = "").isThreeDSecureOneRequired)
    }

    @DisplayName("Given is3dSecureRequired is called, when paReq is null, return false")
    @Test
    fun returnFalseOnIs3dSecureRequiredWithPaReqNull() {
        assertFalse(Receipt().isThreeDSecureOneRequired)
    }

    @DisplayName("Given is3dSecureRequired is called, when paReq is empty, return false")
    @Test
    fun returnFalseOnIs3dSecureRequiredWithPaReqEmpty() {
        assertFalse(Receipt(paReq = "").isThreeDSecureOneRequired)
    }

    @DisplayName("Given is3dSecureRequired is called, when all 3d secure fields are valid, then return true")
    @Test
    fun returnTrueOnIs3dSecureRequiredWithAll3dSecureFieldsValid() {
        assertTrue(Receipt(acsUrl = "1", md = "2", paReq = "3").isThreeDSecureOneRequired)
    }

    @DisplayName("Given toJudoResult is called, then map receiptId to JudoResult receiptId")
    @Test
    fun mapReceiptIdToJudoResultReceiptId() {
        val receiptId = "receiptId"
        assertEquals(receiptId, Receipt(receiptId = receiptId).toJudoResult().receiptId)
    }

    @DisplayName("Given toJudoResult is called, then map originalReceiptId to JudoResult originalReceiptId")
    @Test
    fun mapOriginalReceiptIdToJudoResultoriginalReceiptId() {
        val originalReceiptId = "originalReceiptId"
        assertEquals(
            originalReceiptId,
            Receipt(originalReceiptId = originalReceiptId).toJudoResult().originalReceiptId
        )
    }

    @DisplayName("Given toJudoResult is called, then map partnerServiceFee to JudoResult partnerServiceFee")
    @Test
    fun mapPartnerServiceFeeToJudoResultPartnerServiceFee() {
        val partnerServiceFee = "partnerServiceFee"
        assertEquals(
            partnerServiceFee,
            Receipt(partnerServiceFee = partnerServiceFee).toJudoResult().partnerServiceFee
        )
    }

    @DisplayName("Given toJudoResult is called, then map yourPaymentReference to JudoResult yourPaymentReference")
    @Test
    fun mapYourPaymentReferenceToJudoResultYourPaymentReference() {
        val yourPaymentReference = "yourPaymentReference"
        assertEquals(
            yourPaymentReference,
            Receipt(yourPaymentReference = yourPaymentReference).toJudoResult().yourPaymentReference
        )
    }

    @DisplayName("Given toJudoResult is called, then map type to JudoResult type")
    @Test
    fun mapTypeToJudoResultType() {
        val type = "type"
        assertEquals(type, Receipt(type = type).toJudoResult().type)
    }

    @DisplayName("Given toJudoResult is called, then map createdAt to JudoResult createdAt")
    @Test
    fun mapCreatedAtToJudoResultCreatedAt() {
        val createdAt = Date()
        assertEquals(createdAt, Receipt(createdAt = createdAt).toJudoResult().createdAt)
    }

    @DisplayName("Given toJudoResult is called, then map merchantName to JudoResult merchantName")
    @Test
    fun mapMerchantNameToJudoResultMerchantName() {
        val merchantName = "merchantName"
        assertEquals(merchantName, Receipt(merchantName = merchantName).toJudoResult().merchantName)
    }

    @DisplayName("Given toJudoResult is called, then map appearsOnStatementAs to JudoResult appearsOnStatementAs")
    @Test
    fun mapAppearsOnStatementAsToJudoResultAppearsOnStatementAs() {
        val appearsOnStatementAs = "appearsOnStatementAs"
        assertEquals(
            appearsOnStatementAs,
            Receipt(appearsOnStatementAs = appearsOnStatementAs).toJudoResult().appearsOnStatementAs
        )
    }

    @DisplayName("Given toJudoResult is called, then map originalAmount to JudoResult originalAmount")
    @Test
    fun mapOriginalAmountToJudoResultOriginalAmount() {
        val originalAmount = BigDecimal(1)
        assertEquals(
            originalAmount,
            Receipt(originalAmount = originalAmount).toJudoResult().originalAmount
        )
    }

    @DisplayName("Given toJudoResult is called, then map netAmount to JudoResult netAmount")
    @Test
    fun mapNetAmountToJudoResultNetAmount() {
        val netAmount = BigDecimal(1)
        assertEquals(netAmount, Receipt(netAmount = netAmount).toJudoResult().netAmount)
    }

    @DisplayName("Given toJudoResult is called, then map amount to JudoResult amount")
    @Test
    fun mapAmountToJudoResultAmount() {
        val amount = BigDecimal(1)
        assertEquals(amount, Receipt(amount = amount).toJudoResult().amount)
    }

    @DisplayName("Given toJudoResult is called, then map currency to JudoResult currency")
    @Test
    fun mapCurrencyToJudoResultCurrency() {
        val currency = "GBP"
        assertEquals(currency, Receipt(currency = currency).toJudoResult().currency)
    }

    @DisplayName("Given toJudoResult is called, then map cardDetails to JudoResult cardDetails")
    @Test
    fun mapCardDetailsToJudoResultCardDetails() {
        val cardDetails = mockk<CardToken>()
        assertEquals(cardDetails, Receipt(cardDetails = cardDetails).toJudoResult().cardDetails)
    }

    @DisplayName("Given toJudoResult is called, then map consumer to JudoResult consumer")
    @Test
    fun mapConsumerToJudoResultConsumer() {
        val consumer = mockk<Consumer>()
        assertEquals(consumer, Receipt(consumer = consumer).toJudoResult().consumer)
    }

    @DisplayName("Given toJudoResult is called, then map result to JudoResult result")
    @Test
    fun mapResultToJudoResultResult() {
        val result = "result"
        assertEquals(result, Receipt(result = result).toJudoResult().result)
    }

    @DisplayName("Given toJudoResult is called, then map judoId to JudoResult judoId string")
    @Test
    fun mapResultToJudoResultJudoId() {
        val judoId: Long = 123456
        assertEquals(judoId.toString(), Receipt(judoId = judoId).toJudoResult().judoId)
    }

    @DisplayName("Given toJudoResult is called, then map message to JudoResult message")
    @Test
    fun mapResultToJudoResultMessage() {
        val message = "Card Declined"
        assertEquals(message, Receipt(message = message).toJudoResult().message)
    }

    @DisplayName("Given toCardVerificationModel is called, then map fields to CardVerificationModel")
    @Test
    fun mapFieldsToCardVerificationModel() {
        val receiptId = "receiptId"
        val acsUrl = "acsUrl"
        val paReq = "paReq"
        val md = "md"
        val receipt = Receipt(receiptId = receiptId, md = md, paReq = paReq, acsUrl = acsUrl)

        assertEquals(receiptId, receipt.toCardVerificationModel().receiptId)
        assertEquals(acsUrl, receipt.toCardVerificationModel().acsUrl)
        assertEquals(paReq, receipt.toCardVerificationModel().paReq)
        assertEquals(md, receipt.toCardVerificationModel().md)
    }
}
