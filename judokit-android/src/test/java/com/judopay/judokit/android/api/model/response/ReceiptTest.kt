package com.judopay.judokit.android.api.model.response

import android.util.Base64
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Date

@DisplayName("Testing Receipt model")
internal class ReceiptTest {
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
            Receipt(originalReceiptId = originalReceiptId).toJudoResult().originalReceiptId,
        )
    }

    @DisplayName("Given toJudoResult is called, then map partnerServiceFee to JudoResult partnerServiceFee")
    @Test
    fun mapPartnerServiceFeeToJudoResultPartnerServiceFee() {
        val partnerServiceFee = "partnerServiceFee"
        assertEquals(
            partnerServiceFee,
            Receipt(partnerServiceFee = partnerServiceFee).toJudoResult().partnerServiceFee,
        )
    }

    @DisplayName("Given toJudoResult is called, then map yourPaymentReference to JudoResult yourPaymentReference")
    @Test
    fun mapYourPaymentReferenceToJudoResultYourPaymentReference() {
        val yourPaymentReference = "yourPaymentReference"
        assertEquals(
            yourPaymentReference,
            Receipt(yourPaymentReference = yourPaymentReference).toJudoResult().yourPaymentReference,
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
            Receipt(appearsOnStatementAs = appearsOnStatementAs).toJudoResult().appearsOnStatementAs,
        )
    }

    @DisplayName("Given toJudoResult is called, then map originalAmount to JudoResult originalAmount")
    @Test
    fun mapOriginalAmountToJudoResultOriginalAmount() {
        val originalAmount = BigDecimal(1)
        assertEquals(
            originalAmount,
            Receipt(originalAmount = originalAmount).toJudoResult().originalAmount,
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

    @DisplayName("Given result is Declined with soft decline message and receiptId, isSoftDeclined is true")
    @Test
    fun isSoftDeclinedTrueWhenConditionsMet() {
        val receipt =
            Receipt(
                result = "Declined",
                message = "Card declined: Additional customer authentication required",
                receiptId = "receipt-123",
            )
        assertTrue(receipt.isSoftDeclined)
    }

    @DisplayName("Given result is not Declined, isSoftDeclined is false")
    @Test
    fun isSoftDeclinedFalseWhenResultNotDeclined() {
        val receipt =
            Receipt(
                result = "Success",
                message = "Card declined: Additional customer authentication required",
                receiptId = "receipt-123",
            )
        assertFalse(receipt.isSoftDeclined)
    }

    @DisplayName("Given receiptId is null, isSoftDeclined is false")
    @Test
    fun isSoftDeclinedFalseWhenReceiptIdNull() {
        val receipt =
            Receipt(
                result = "Declined",
                message = "Card declined: Additional customer authentication required",
                receiptId = null,
            )
        assertFalse(receipt.isSoftDeclined)
    }

    @DisplayName("Given message contains challenge required text, isThreeDSecureTwoRequired is true")
    @Test
    fun isThreeDSecureTwoRequiredTrueWhenChallengeMessage() {
        val receipt = Receipt(message = "Issuer ACS has responded with a Challenge URL")
        assertTrue(receipt.isThreeDSecureTwoRequired)
    }

    @DisplayName("Given message does not match, isThreeDSecureTwoRequired is false")
    @Test
    fun isThreeDSecureTwoRequiredFalseWhenNonMatchingMessage() {
        val receipt = Receipt(message = "Payment successful")
        assertFalse(receipt.isThreeDSecureTwoRequired)
    }

    @DisplayName("Receipt toString() contains key fields")
    @Test
    fun receiptToStringContainsFields() {
        val receipt = Receipt(receiptId = "R123", result = "Success", currency = "GBP")
        val str = receipt.toString()
        assertTrue(str.contains("R123"))
        assertTrue(str.contains("Success"))
        assertTrue(str.contains("GBP"))
    }

    @DisplayName("getCReqParameters returns parsed CReqParameters on valid base64 JSON")
    @Test
    fun getCReqParametersReturnsParsedParameters() {
        mockkStatic(Base64::class)
        try {
            val json = """{"messageType":"CReq","messageVersion":"2.1.0","threeDSServerTransID":"srv-id","acsTransID":"acs-id"}"""
            every { Base64.decode(any<String>(), any()) } returns json.toByteArray()
            val params = Receipt(cReq = "dummybase64").getCReqParameters()
            assertNotNull(params)
            assertEquals("CReq", params?.messageType)
            assertEquals("2.1.0", params?.messageVersion)
            assertEquals("srv-id", params?.threeDSServerTransID)
            assertEquals("acs-id", params?.acsTransID)
        } finally {
            unmockkStatic(Base64::class)
        }
    }

    @DisplayName("getCReqParameters returns null when base64 decodes to malformed JSON")
    @Test
    fun getCReqParametersReturnsNullOnMalformedJson() {
        mockkStatic(Base64::class)
        try {
            every { Base64.decode(any<String>(), any()) } returns "not-valid-json".toByteArray()
            val params = Receipt(cReq = "dummybase64").getCReqParameters()
            assertNull(params)
        } finally {
            unmockkStatic(Base64::class)
        }
    }

    @DisplayName("getChallengeParameters maps cReq fields correctly")
    @Test
    fun getChallengeParametersMapsFields() {
        mockkStatic(Base64::class)
        try {
            val json = """{"messageType":"CReq","messageVersion":"2.2.0","threeDSServerTransID":"srv-123","acsTransID":"acs-456"}"""
            every { Base64.decode(any<String>(), any()) } returns json.toByteArray()
            val receipt =
                Receipt(
                    cReq = "dummybase64",
                    acsReferenceNumber = "ref-001",
                    acsSignedContent = "signed-content",
                )
            val challengeParams = receipt.getChallengeParameters()
            assertEquals("srv-123", challengeParams.threeDSServerTransactionID)
            assertEquals("acs-456", challengeParams.acsTransactionID)
        } finally {
            unmockkStatic(Base64::class)
        }
    }
}
