package com.judopay.judokit.android.api.model.request.threedsecure

import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.ScaExemption
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing ThreeDSecureTwo.Builder")
internal class ThreeDSecureTwoTest {
    private val validSdkParameters =
        SdkParameters
            .Builder()
            .setApplicationId("appId")
            .setEncodedData("encodedData")
            .setEphemeralPublicKey(EphemeralPublicKey(kty = "EC", crv = "P-256", x = "x", y = "y"))
            .setMaxTimeout(5)
            .setReferenceNumber("ref123")
            .setTransactionId("txn123")
            .build()

    @Test
    @DisplayName("Given required sdkParameters, then build() succeeds")
    fun buildWithRequiredFields() {
        val result =
            ThreeDSecureTwo
                .Builder()
                .setSdkParameters(validSdkParameters)
                .build()
        assertNotNull(result)
    }

    @Test
    @DisplayName("Given all fields, then build() succeeds")
    fun buildWithAllFields() {
        val result =
            ThreeDSecureTwo
                .Builder()
                .setSdkParameters(validSdkParameters)
                .setChallengeRequestIndicator(ChallengeRequestIndicator.NO_PREFERENCE)
                .setScaExemption(ScaExemption.LOW_VALUE)
                .setSoftDeclineReceiptId("receipt123")
                .build()
        assertNotNull(result)
    }

    @Test
    @DisplayName("Given null sdkParameters, then build() throws IllegalArgumentException")
    fun throwsWhenSdkParametersIsNull() {
        assertThrows<IllegalArgumentException> {
            ThreeDSecureTwo
                .Builder()
                .setSdkParameters(null)
                .build()
        }
    }
}
