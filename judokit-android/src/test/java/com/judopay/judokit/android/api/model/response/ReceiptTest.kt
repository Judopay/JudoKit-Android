package com.judopay.judokit.android.api.model.response

import android.util.Base64
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Date

@DisplayName("Testing Receipt model")
internal class ReceiptTest {
    @AfterEach
    fun tearDown() {
        unmockkAll()
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

    @Nested
    @DisplayName("isSoftDeclined edge cases")
    inner class IsSoftDeclinedEdgeCases {
        @DisplayName("Given message does not match soft decline text, isSoftDeclined is false")
        @Test
        fun isSoftDeclinedFalseWhenMessageDoesNotMatch() {
            val receipt =
                Receipt(
                    result = "Declined",
                    message = "Card declined: Generic error",
                    receiptId = "receipt-123",
                )
            assertFalse(receipt.isSoftDeclined)
        }

        @DisplayName("Given receiptId is empty string, isSoftDeclined is false")
        @Test
        fun isSoftDeclinedFalseWhenReceiptIdEmpty() {
            val receipt =
                Receipt(
                    result = "Declined",
                    message = "Card declined: Additional customer authentication required",
                    receiptId = "",
                )
            assertFalse(receipt.isSoftDeclined)
        }

        @DisplayName("Given result is Declined in lowercase, isSoftDeclined is still true (case-insensitive)")
        @Test
        fun isSoftDeclinedCaseInsensitiveResult() {
            val receipt =
                Receipt(
                    result = "declined",
                    message = "card declined: additional customer authentication required",
                    receiptId = "receipt-123",
                )
            assertTrue(receipt.isSoftDeclined)
        }

        @DisplayName("Given message is null, isSoftDeclined is false")
        @Test
        fun isSoftDeclinedFalseWhenMessageNull() {
            val receipt =
                Receipt(
                    result = "Declined",
                    message = null,
                    receiptId = "receipt-123",
                )
            assertFalse(receipt.isSoftDeclined)
        }
    }

    @Nested
    @DisplayName("isThreeDSecureTwoRequired edge cases")
    inner class IsThreeDSecureTwoRequiredEdgeCases {
        @DisplayName("Given message is null, isThreeDSecureTwoRequired is false")
        @Test
        fun isThreeDSecureTwoRequiredFalseWhenMessageNull() {
            val receipt = Receipt(message = null)
            assertFalse(receipt.isThreeDSecureTwoRequired)
        }

        @DisplayName("Given message matches in different case, isThreeDSecureTwoRequired is true (case-insensitive)")
        @Test
        fun isThreeDSecureTwoRequiredCaseInsensitive() {
            val receipt = Receipt(message = "ISSUER ACS HAS RESPONDED WITH A CHALLENGE URL")
            assertTrue(receipt.isThreeDSecureTwoRequired)
        }

        @DisplayName("Given message is empty, isThreeDSecureTwoRequired is false")
        @Test
        fun isThreeDSecureTwoRequiredFalseWhenMessageEmpty() {
            val receipt = Receipt(message = "")
            assertFalse(receipt.isThreeDSecureTwoRequired)
        }
    }

    @Nested
    @DisplayName("toJudoResult additional field mappings")
    inner class ToJudoResultAdditionalFields {
        @DisplayName("Given toJudoResult is called, then map yourPaymentMetaData to JudoResult")
        @Test
        fun mapYourPaymentMetaDataToJudoResult() {
            val metaData = mapOf("key1" to "value1", "key2" to "value2")
            assertEquals(metaData, Receipt(yourPaymentMetaData = metaData).toJudoResult().yourPaymentMetaData)
        }

        @DisplayName("Given toJudoResult is called, then map emailAddress to JudoResult")
        @Test
        fun mapEmailAddressToJudoResult() {
            val email = "test@example.com"
            assertEquals(email, Receipt(emailAddress = email).toJudoResult().emailAddress)
        }

        @DisplayName("Given toJudoResult is called, then map networkTokenisationDetails to JudoResult")
        @Test
        fun mapNetworkTokenisationDetailsToJudoResult() {
            val details = mockk<NetworkTokenisationDetails>()
            assertEquals(details, Receipt(networkTokenisationDetails = details).toJudoResult().networkTokenisationDetails)
        }

        @DisplayName("Given toJudoResult is called, then map threeDSecure to JudoResult")
        @Test
        fun mapThreeDSecureToJudoResult() {
            val threeDSecure = mockk<ThreeDSecure>()
            assertEquals(threeDSecure, Receipt(threeDSecure = threeDSecure).toJudoResult().threeDSecure)
        }

        @DisplayName("Given judoId is null, toJudoResult maps judoId as 'null' string")
        @Test
        fun mapNullJudoIdToJudoResultAsNullString() {
            assertEquals("null", Receipt(judoId = null).toJudoResult().judoId)
        }
    }

    @Nested
    @DisplayName("toCardVerificationModel field validation")
    inner class ToCardVerificationModelFieldValidation {
        @DisplayName("Given required fields are null, toCardVerificationModel throws IllegalArgumentException")
        @Test
        fun nullFieldsThrowIllegalArgumentException() {
            assertThrows(IllegalArgumentException::class.java) {
                Receipt().toCardVerificationModel()
            }
        }
    }

    @Nested
    @DisplayName("getCReqParameters edge cases")
    inner class GetCReqParametersEdgeCases {
        @DisplayName("getCReqParameters returns partial nulls when JSON fields are missing")
        @Test
        fun getCReqParametersHandlesMissingJsonFields() {
            mockkStatic(Base64::class)
            try {
                val json = """{"messageType":"CReq","messageVersion":"2.1.0"}"""
                every { Base64.decode(any<String>(), any()) } returns json.toByteArray()
                val params = Receipt(cReq = "dummybase64").getCReqParameters()
                assertNotNull(params)
                assertEquals("CReq", params?.messageType)
                assertNull(params?.threeDSServerTransID)
                assertNull(params?.acsTransID)
            } finally {
                unmockkStatic(Base64::class)
            }
        }
    }

    @Nested
    @DisplayName("getChallengeParameters edge cases")
    inner class GetChallengeParametersEdgeCases {
        @DisplayName("Given explicit null CReqParameters, getChallengeParameters maps acsReferenceNumber and acsSignedContent")
        @Test
        fun getChallengeParametersWithNullCReqParams() {
            val receipt =
                Receipt(
                    acsReferenceNumber = "ref-999",
                    acsSignedContent = "signed-xyz",
                )
            val challengeParams = receipt.getChallengeParameters(cReqParams = null)
            assertNull(challengeParams.threeDSServerTransactionID)
            assertNull(challengeParams.acsTransactionID)
            assertEquals("ref-999", challengeParams.acsRefNumber)
            assertEquals("signed-xyz", challengeParams.acsSignedContent)
        }

        @DisplayName("Given explicit CReqParameters, getChallengeParameters uses them without decoding cReq")
        @Test
        fun getChallengeParametersWithExplicitCReqParams() {
            val cReqParams =
                CReqParameters(
                    messageType = "CReq",
                    messageVersion = "2.2.0",
                    threeDSServerTransID = "explicit-srv",
                    acsTransID = "explicit-acs",
                )
            val receipt =
                Receipt(
                    acsReferenceNumber = "ref-001",
                    acsSignedContent = "content",
                )
            val challengeParams = receipt.getChallengeParameters(cReqParams = cReqParams)
            assertEquals("explicit-srv", challengeParams.threeDSServerTransactionID)
            assertEquals("explicit-acs", challengeParams.acsTransactionID)
            assertEquals("ref-001", challengeParams.acsRefNumber)
            assertEquals("content", challengeParams.acsSignedContent)
        }
    }

    @Nested
    @DisplayName("toString")
    inner class ToStringTests {
        @DisplayName("toString on a Receipt with all null fields does not throw")
        @Test
        fun toStringWithAllNullFields() {
            val str = Receipt().toString()
            assertNotNull(str)
            assertTrue(str.contains("Receipt("))
        }

        @DisplayName("toString includes emailAddress and disableNetworkTokenisation fields")
        @Test
        fun toStringIncludesEmailAndTokenisationFields() {
            val receipt =
                Receipt(
                    emailAddress = "user@example.com",
                    disableNetworkTokenisation = true,
                )
            val str = receipt.toString()
            assertTrue(str.contains("user@example.com"))
            assertTrue(str.contains("true"))
        }
    }
}
