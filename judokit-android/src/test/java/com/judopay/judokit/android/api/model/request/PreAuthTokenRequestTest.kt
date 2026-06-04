package com.judopay.judokit.android.api.model.request

import com.judopay.judokit.android.api.model.request.threedsecure.DeviceRenderOptions
import com.judopay.judokit.android.api.model.request.threedsecure.EphemeralPublicKey
import com.judopay.judokit.android.api.model.request.threedsecure.SdkParameters
import com.judopay.judokit.android.api.model.request.threedsecure.ThreeDSecureTwo
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing PreAuthTokenRequest.Builder")
internal class PreAuthTokenRequestTest {
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
        PreAuthTokenRequest
            .Builder()
            .setJudoId("100200300")
            .setAmount("1.00")
            .setCurrency("GBP")
            .setYourConsumerReference("consumer-ref")
            .setYourPaymentReference("payment-ref")
            .setCardToken("tok_abc123")
            .setThreeDSecure(threeDSecure)

    @Test
    @DisplayName("Given all required fields, then build() succeeds")
    fun buildWithRequiredFields() {
        assertNotNull(validBuilder().build())
    }

    @Test
    @DisplayName("Given optional fields, then build() succeeds")
    fun buildWithOptionalFields() {
        val request =
            validBuilder()
                .setEndDate("12/25")
                .setCardLastFour("1111")
                .setCardType(1)
                .setCv2("452")
                .setDelayedAuthorisation(true)
                .setAllowIncrement(false)
                .setDisableNetworkTokenisation(true)
                .build()
        assertNotNull(request)
    }

    @Test
    @DisplayName("Given null cardToken, then build() throws IllegalArgumentException")
    fun throwsWhenCardTokenIsNull() {
        assertThrows<IllegalArgumentException> {
            validBuilder().setCardToken(null).build()
        }
    }

    @Test
    @DisplayName("Given null threeDSecure, then build() throws IllegalArgumentException")
    fun throwsWhenThreeDSecureIsNull() {
        assertThrows<IllegalArgumentException> {
            validBuilder().setThreeDSecure(null).build()
        }
    }
}
