package com.judopay.judokit.android.api.model.request.threedsecure

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing SdkParameters.Builder")
internal class SdkParametersTest {
    private val validEphemeralPublicKey =
        EphemeralPublicKey(
            kty = "EC",
            crv = "P-256",
            x = "test_x",
            y = "test_y",
        )

    private fun validBuilder() =
        SdkParameters
            .Builder()
            .setApplicationId("appId123")
            .setEncodedData("encodedData123")
            .setEphemeralPublicKey(validEphemeralPublicKey)
            .setMaxTimeout(5)
            .setReferenceNumber("ref123")
            .setTransactionId("txn123")

    @Test
    @DisplayName("Given all required fields, then build() succeeds")
    fun buildWithAllRequiredFields() {
        val params = validBuilder().build()
        assertNotNull(params)
    }

    @Test
    @DisplayName("Given default DeviceRenderOptions when not set, then build() succeeds")
    fun buildUsesDefaultDeviceRenderOptions() {
        val params = validBuilder().build()
        assertNotNull(params)
    }

    @Test
    @DisplayName("Given custom DeviceRenderOptions, then build() succeeds")
    fun buildWithCustomDeviceRenderOptions() {
        val params =
            validBuilder()
                .setDeviceRenderOptions(DeviceRenderOptions())
                .build()
        assertNotNull(params)
    }

    @Test
    @DisplayName("Given null applicationId, then build() throws IllegalArgumentException")
    fun throwsWhenApplicationIdIsNull() {
        assertThrows<IllegalArgumentException> {
            SdkParameters
                .Builder()
                .setApplicationId(null)
                .setEncodedData("encodedData")
                .setEphemeralPublicKey(validEphemeralPublicKey)
                .setMaxTimeout(5)
                .setReferenceNumber("ref123")
                .setTransactionId("txn123")
                .build()
        }
    }

    @Test
    @DisplayName("Given empty encodedData, then build() throws IllegalArgumentException")
    fun throwsWhenEncodedDataIsEmpty() {
        assertThrows<IllegalArgumentException> {
            SdkParameters
                .Builder()
                .setApplicationId("appId")
                .setEncodedData("")
                .setEphemeralPublicKey(validEphemeralPublicKey)
                .setMaxTimeout(5)
                .setReferenceNumber("ref123")
                .setTransactionId("txn123")
                .build()
        }
    }

    @Test
    @DisplayName("Given null ephemeralPublicKey, then build() throws IllegalArgumentException")
    fun throwsWhenEphemeralPublicKeyIsNull() {
        assertThrows<IllegalArgumentException> {
            SdkParameters
                .Builder()
                .setApplicationId("appId")
                .setEncodedData("data")
                .setEphemeralPublicKey(null)
                .setMaxTimeout(5)
                .setReferenceNumber("ref123")
                .setTransactionId("txn123")
                .build()
        }
    }

    @Test
    @DisplayName("Given null maxTimeout, then build() throws IllegalArgumentException")
    fun throwsWhenMaxTimeoutIsNull() {
        assertThrows<IllegalArgumentException> {
            SdkParameters
                .Builder()
                .setApplicationId("appId")
                .setEncodedData("data")
                .setEphemeralPublicKey(validEphemeralPublicKey)
                .setMaxTimeout(null)
                .setReferenceNumber("ref123")
                .setTransactionId("txn123")
                .build()
        }
    }

    @Test
    @DisplayName("Given empty transactionId, then build() throws IllegalArgumentException")
    fun throwsWhenTransactionIdIsEmpty() {
        assertThrows<IllegalArgumentException> {
            SdkParameters
                .Builder()
                .setApplicationId("appId")
                .setEncodedData("data")
                .setEphemeralPublicKey(validEphemeralPublicKey)
                .setMaxTimeout(5)
                .setReferenceNumber("ref123")
                .setTransactionId("")
                .build()
        }
    }
}
