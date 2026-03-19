package com.judopay.judokit.android.api.model.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing response model classes")
internal class ResponseModelsTest {
    @Test
    @DisplayName("Receipts stores all fields correctly")
    fun receiptsStoresFields() {
        val receipts =
            Receipts(
                resultCount = 5,
                pageSize = 10,
                offset = 0,
                results = emptyList(),
            )
        assertEquals(5, receipts.resultCount)
        assertEquals(10, receipts.pageSize)
        assertEquals(0, receipts.offset)
        assertTrue(receipts.results.isEmpty())
    }

    @Test
    @DisplayName("Receipts toString() contains field values")
    fun receiptsToString() {
        val receipts = Receipts(resultCount = 3, pageSize = 5, offset = 0, results = emptyList())
        val str = receipts.toString()
        assertTrue(str.contains("3"))
        assertTrue(str.contains("5"))
    }

    @Test
    @DisplayName("ThreeDSecure stores all fields correctly")
    fun threeDSecureStoresFields() {
        val threeDSecure =
            ThreeDSecure(
                attempted = true,
                result = "PASSED",
                eci = "05",
                challengeRequestIndicator = "NO_PREFERENCE",
                challengeCompleted = true,
            )
        assertEquals(true, threeDSecure.attempted)
        assertEquals("PASSED", threeDSecure.result)
        assertEquals("05", threeDSecure.eci)
        assertTrue(threeDSecure.challengeCompleted == true)
    }

    @Test
    @DisplayName("ThreeDSecure toString() contains fields")
    fun threeDSecureToString() {
        val threeDSecure = ThreeDSecure(result = "PASSED")
        val str = threeDSecure.toString()
        assertTrue(str.contains("PASSED"))
    }

    @Test
    @DisplayName("ThreeDSecure with all nulls creates correctly")
    fun threeDSecureAllNulls() {
        val threeDSecure = ThreeDSecure()
        assertNull(threeDSecure.attempted)
        assertNull(threeDSecure.result)
    }

    @Test
    @DisplayName("Risks stores postCodeCheck correctly")
    fun risksStoresPostCodeCheck() {
        val risks = Risks(postCodeCheck = "PASSED")
        assertEquals("PASSED", risks.postCodeCheck)
    }

    @Test
    @DisplayName("Risks toString() contains field values")
    fun risksToString() {
        val risks = Risks(postCodeCheck = "FAILED")
        assertTrue(risks.toString().contains("FAILED"))
    }

    @Test
    @DisplayName("Risks with null postCodeCheck creates correctly")
    fun risksWithNullPostCodeCheck() {
        val risks = Risks()
        assertNull(risks.postCodeCheck)
    }

    @Test
    @DisplayName("VirtualPan stores fields correctly")
    fun virtualPanStoresFields() {
        val virtualPan = VirtualPan(lastFour = "1234", expiryDate = "12/25")
        assertEquals("1234", virtualPan.lastFour)
        assertEquals("12/25", virtualPan.expiryDate)
    }

    @Test
    @DisplayName("VirtualPan toString() contains field values")
    fun virtualPanToString() {
        val virtualPan = VirtualPan(lastFour = "5678")
        assertTrue(virtualPan.toString().contains("5678"))
    }

    @Test
    @DisplayName("AcsInterface enum values are accessible")
    fun acsInterfaceValues() {
        assertEquals(2, AcsInterface.values().size)
        assertEquals(AcsInterface.NATIVE_UI, AcsInterface.valueOf("NATIVE_UI"))
        assertEquals(AcsInterface.HTML_UI, AcsInterface.valueOf("HTML_UI"))
    }

    @Test
    @DisplayName("Consumer stores fields correctly")
    fun consumerStoresFields() {
        @Suppress("DEPRECATION")
        val consumer = Consumer(consumerToken = "tok_123", yourConsumerReference = "ref-456")
        @Suppress("DEPRECATION")
        assertEquals("tok_123", consumer.consumerToken)
        assertEquals("ref-456", consumer.yourConsumerReference)
    }

    @Test
    @DisplayName("Response toString() contains all fields")
    fun responseToString() {
        val response =
            Response(
                result = "Success",
                message = "Payment complete",
            )
        val str = response.toString()
        assertTrue(str.contains("Success"))
        assertTrue(str.contains("Payment complete"))
    }

    @Test
    @DisplayName("NetworkTokenisationDetails stores all fields correctly")
    fun networkTokenisationDetailsStoresFields() {
        val virtualPan = VirtualPan(lastFour = "1234", expiryDate = "12/25")
        val details =
            NetworkTokenisationDetails(
                networkTokenProvisioned = true,
                networkTokenUsed = false,
                virtualPan = virtualPan,
                accountDetailsUpdated = true,
            )
        assertTrue(details.networkTokenProvisioned == true)
        assertFalse(details.networkTokenUsed == true)
        assertEquals("1234", details.virtualPan?.lastFour)
        assertTrue(details.accountDetailsUpdated == true)
    }

    @Test
    @DisplayName("NetworkTokenisationDetails toString() contains fields")
    fun networkTokenisationDetailsToString() {
        val details = NetworkTokenisationDetails(networkTokenProvisioned = true, networkTokenUsed = false)
        val str = details.toString()
        assertTrue(str.contains("true"))
    }

    @Test
    @DisplayName("NetworkTokenisationDetails with default nulls creates correctly")
    fun networkTokenisationDetailsAllNulls() {
        val details = NetworkTokenisationDetails()
        assertNull(details.networkTokenProvisioned)
        assertNull(details.networkTokenUsed)
        assertNull(details.virtualPan)
        assertNull(details.accountDetailsUpdated)
    }

    @Test
    @DisplayName("CReqParameters stores all fields correctly")
    fun cReqParametersStoresFields() {
        val params =
            CReqParameters(
                messageType = "CReq",
                messageVersion = "2.1.0",
                threeDSServerTransID = "server-txn-id",
                acsTransID = "acs-txn-id",
            )
        assertEquals("CReq", params.messageType)
        assertEquals("2.1.0", params.messageVersion)
        assertEquals("server-txn-id", params.threeDSServerTransID)
        assertEquals("acs-txn-id", params.acsTransID)
    }
}
