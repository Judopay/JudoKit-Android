package com.judopay.judokit.android.api.model.request

import com.judopay.judokit.android.api.model.request.threedsecure.DeviceRenderOptions
import com.judopay.judokit.android.api.model.request.threedsecure.EphemeralPublicKey
import com.judopay.judokit.android.api.model.request.threedsecure.SdkParameters
import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecureTwo
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing PreAuthRequest.Builder")
internal class PreAuthRequestTest {
    private val threeDSecure =
        ThreeDSecureTwo
            .Builder()
            .setSdkParameters(
                SdkParameters
                    .Builder()
                    .setApplicationId("appId")
                    .setEncodedData("data")
                    .setEphemeralPublicKey(EphemeralPublicKey("EC", "P-256", "x", "y"))
                    .setMaxTimeout(5)
                    .setReferenceNumber("ref")
                    .setTransactionId("txn")
                    .setDeviceRenderOptions(DeviceRenderOptions())
                    .build(),
            ).build()

    private fun validBuilder() =
        PreAuthRequest
            .Builder()
            .setJudoId("100200300")
            .setAmount("1.00")
            .setCurrency("GBP")
            .setYourConsumerReference("consumer-ref")
            .setYourPaymentReference("payment-ref")
            .setCardNumber("4111111111111111")
            .setCv2("452")
            .setExpiryDate("12/25")
            .setThreeDSecure(threeDSecure)

    @Test
    @DisplayName("Given all required fields, then build() succeeds")
    fun buildWithRequiredFields() {
        assertNotNull(validBuilder().build())
    }

    @Test
    @DisplayName("Given optional fields are set, then build() succeeds")
    fun buildWithOptionalFields() {
        val request =
            validBuilder()
                .setDelayedAuthorisation(true)
                .setAllowIncrement(true)
                .setDisableNetworkTokenisation(true)
                .setCardHolderName("John Doe")
                .setMobileNumber("1234567890")
                .setEmailAddress("test@example.com")
                .build()
        assertNotNull(request)
    }

    @Test
    @DisplayName("Given null judoId, then build() throws IllegalArgumentException")
    fun throwsWhenJudoIdIsNull() {
        assertThrows<IllegalArgumentException> {
            validBuilder().setJudoId(null).build()
        }
    }

    @Test
    @DisplayName("Given null threeDSecure, then build() throws IllegalArgumentException")
    fun throwsWhenThreeDSecureIsNull() {
        assertThrows<IllegalArgumentException> {
            validBuilder().setThreeDSecure(null).build()
        }
    }

    @Test
    @DisplayName("Given null cardNumber, then build() throws IllegalArgumentException")
    fun throwsWhenCardNumberIsNull() {
        assertThrows<IllegalArgumentException> {
            validBuilder().setCardNumber(null).build()
        }
    }
}
