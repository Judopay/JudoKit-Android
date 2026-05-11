package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing RecommendationConfiguration.Builder")
internal class RecommendationConfigurationTest {
    private val validUrl = "https://recommendation.example.com/api"
    private val validRsaKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA"

    @Test
    @DisplayName("Given valid url and rsaPublicKey, then build() returns correct configuration")
    fun buildWithValidFields() {
        val config =
            RecommendationConfiguration
                .Builder()
                .setUrl(validUrl)
                .setRsaPublicKey(validRsaKey)
                .build()
        assertEquals(validUrl, config.url)
        assertEquals(validRsaKey, config.rsaPublicKey)
        assertNull(config.timeout)
        assertFalse(config.shouldHaltTransactionInCaseOfAnyError)
    }

    @Test
    @DisplayName("Given timeout and shouldHaltTransaction are set, then build() includes them")
    fun buildWithOptionalFields() {
        val config =
            RecommendationConfiguration
                .Builder()
                .setUrl(validUrl)
                .setRsaPublicKey(validRsaKey)
                .setTimeout(5000)
                .setShouldHaltTransactionInCaseOfAnyError(true)
                .build()
        assertEquals(5000, config.timeout)
        assertTrue(config.shouldHaltTransactionInCaseOfAnyError)
    }

    @Test
    @DisplayName("Given null rsaPublicKey, then build() throws IllegalArgumentException")
    fun throwsWhenRsaPublicKeyIsNull() {
        assertThrows<IllegalArgumentException> {
            RecommendationConfiguration
                .Builder()
                .setUrl(validUrl)
                .setRsaPublicKey(null)
                .build()
        }
    }

    @Test
    @DisplayName("Given empty rsaPublicKey, then build() throws IllegalArgumentException")
    fun throwsWhenRsaPublicKeyIsEmpty() {
        assertThrows<IllegalArgumentException> {
            RecommendationConfiguration
                .Builder()
                .setUrl(validUrl)
                .setRsaPublicKey("")
                .build()
        }
    }

    @Test
    @DisplayName("Given null url, then build() throws IllegalArgumentException")
    fun throwsWhenUrlIsNull() {
        assertThrows<IllegalArgumentException> {
            RecommendationConfiguration
                .Builder()
                .setUrl(null)
                .setRsaPublicKey(validRsaKey)
                .build()
        }
    }

    @Test
    @DisplayName("Given invalid url (no https), then build() throws IllegalArgumentException")
    fun throwsWhenUrlIsInvalid() {
        assertThrows<IllegalArgumentException> {
            RecommendationConfiguration
                .Builder()
                .setUrl("not-a-valid-url")
                .setRsaPublicKey(validRsaKey)
                .build()
        }
    }

    @Test
    @DisplayName("Given valid config, then toString contains url and rsaPublicKey")
    fun toStringContainsUrlAndRsaPublicKey() {
        val config =
            RecommendationConfiguration
                .Builder()
                .setUrl(validUrl)
                .setRsaPublicKey(validRsaKey)
                .build()
        val str = config.toString()
        assertTrue(str.contains(validUrl))
        assertTrue(str.contains(validRsaKey))
    }

    @Test
    @DisplayName("Given config with timeout, then toString contains timeout value")
    fun toStringContainsTimeoutWhenSet() {
        val config =
            RecommendationConfiguration
                .Builder()
                .setUrl(validUrl)
                .setRsaPublicKey(validRsaKey)
                .setTimeout(3000)
                .build()
        val str = config.toString()
        assertTrue(str.contains("3000"))
    }

    @Test
    @DisplayName("Given empty url, then build() throws IllegalArgumentException")
    fun throwsWhenUrlIsEmpty() {
        assertThrows<IllegalArgumentException> {
            RecommendationConfiguration
                .Builder()
                .setUrl("")
                .setRsaPublicKey(validRsaKey)
                .build()
        }
    }
}
