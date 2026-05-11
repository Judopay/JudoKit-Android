package com.judopay.judokit.android.model

import android.content.res.Resources
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing JudoError")
internal class JudoErrorTest {
    private val resources: Resources =
        mockk {
            every { getString(any()) } returns "mocked string"
        }

    @Test
    @DisplayName("userCancelled() returns JudoError with USER_CANCELLED code")
    fun userCancelledReturnsCorrectCode() {
        val error = JudoError.userCancelled()
        assertEquals(USER_CANCELLED, error.code)
    }

    @Test
    @DisplayName("userCancelled() returns JudoError with non-empty message")
    fun userCancelledReturnsNonEmptyMessage() {
        val error = JudoError.userCancelled()
        assertTrue(error.message.isNotEmpty())
    }

    @Test
    @DisplayName("judoInternalError() with message stores message correctly")
    fun judoInternalErrorWithMessage() {
        val error = JudoError.judoInternalError("Something went wrong")
        assertEquals(EXCEPTION_CAUGHT, error.code)
        assertEquals("Something went wrong", error.message)
    }

    @Test
    @DisplayName("judoInternalError() with null message uses 'Unknown error'")
    fun judoInternalErrorWithNullMessage() {
        val error = JudoError.judoInternalError(null)
        assertEquals("Unknown error", error.message)
    }

    @Test
    @DisplayName("JudoError default constructor uses USER_CANCELLED code")
    fun defaultConstructorUsesUserCancelledCode() {
        val error = JudoError()
        assertEquals(USER_CANCELLED, error.code)
    }

    @Test
    @DisplayName("JudoError details list can be populated")
    fun detailsListCanBePopulated() {
        val detail = JudoError(code = 123, message = "detail")
        val error = JudoError(code = 1, message = "main", details = mutableListOf(detail))
        assertEquals(1, error.details.size)
        assertEquals(123, error.details[0].code)
    }

    @Test
    @DisplayName("judoRequestFailedError() returns REQUEST_FAILED code with one detail")
    fun judoRequestFailedError() {
        val error = JudoError.judoRequestFailedError(resources)
        assertEquals(REQUEST_FAILED, error.code)
        assertEquals(1, error.details.size)
        assertEquals(REQUEST_FAILED, error.details[0].code)
    }

    @Test
    @DisplayName("judoNetworkError() returns NETWORK_ERROR code with throwable message as detail")
    fun judoNetworkError() {
        val throwable = RuntimeException("no network")
        val error = JudoError.judoNetworkError(resources, throwable)
        assertEquals(NETWORK_ERROR, error.code)
        assertEquals(1, error.details.size)
        assertEquals("no network", error.details[0].message)
    }

    @Test
    @DisplayName("judoNetworkError() uses fallback message when throwable has no localizedMessage")
    fun judoNetworkErrorFallbackMessage() {
        val throwable =
            object : Throwable() {
                override val message: String? = null
            }
        val error = JudoError.judoNetworkError(resources, throwable)
        assertEquals(NETWORK_ERROR, error.code)
        assertEquals("mocked string", error.details[0].message)
    }

    @Test
    @DisplayName("judoPoorConnectivityError() returns NETWORK_ERROR code with throwable message as detail")
    fun judoPoorConnectivityError() {
        val throwable = RuntimeException("timeout")
        val error = JudoError.judoPoorConnectivityError(resources, throwable)
        assertEquals(NETWORK_ERROR, error.code)
        assertEquals("timeout", error.details[0].message)
    }

    @Test
    @DisplayName("googlePayNotSupported() returns GOOGLE_PAY_NOT_SUPPORTED code")
    fun googlePayNotSupported() {
        val error = JudoError.googlePayNotSupported(resources, "device not supported")
        assertEquals(GOOGLE_PAY_NOT_SUPPORTED, error.code)
        assertEquals("device not supported", error.details[0].message)
    }

    @Test
    @DisplayName("googlePayNotSupported() uses fallback message when message param is null")
    fun googlePayNotSupportedNullMessage() {
        val error = JudoError.googlePayNotSupported(resources, null)
        assertEquals(GOOGLE_PAY_NOT_SUPPORTED, error.code)
        assertEquals("mocked string", error.details[0].message)
    }

    @Test
    @DisplayName("judoRecommendationTransactionPreventedError() returns REQUEST_FAILED code")
    fun judoRecommendationTransactionPreventedError() {
        val error = JudoError.judoRecommendationTransactionPreventedError(resources)
        assertEquals(REQUEST_FAILED, error.code)
        assertEquals(1, error.details.size)
    }

    @Test
    @DisplayName("judoRecommendationRetrievingError() returns REQUEST_FAILED code")
    fun judoRecommendationRetrievingError() {
        val error = JudoError.judoRecommendationRetrievingError(resources)
        assertEquals(REQUEST_FAILED, error.code)
        assertEquals(1, error.details.size)
    }
}
