package com.judopay.judokit.android.api.error

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing ApiError and related classes")
internal class ApiErrorTest {
    @Test
    @DisplayName("ApiError.toJudoError() converts to JudoError with correct code and message")
    fun toJudoErrorConvertsCorrectly() {
        val apiError = ApiError(code = 1234, category = 2, message = "Test error")
        val judoError = apiError.toJudoError()
        assertEquals(1234, judoError.code)
        assertEquals("Test error", judoError.message)
    }

    @Test
    @DisplayName("ApiError toString() contains relevant fields")
    fun apiErrorToString() {
        val apiError = ApiError(code = 100, category = 1, message = "Error message")
        val str = apiError.toString()
        assertTrue(str.contains("100"))
        assertTrue(str.contains("Error message"))
    }

    @Test
    @DisplayName("ApiErrorDetail toString() contains relevant fields")
    fun apiErrorDetailToString() {
        val detail = ApiErrorDetail(code = 50, message = "Field error", fieldName = "cardNumber")
        val str = detail.toString()
        assertTrue(str.contains("cardNumber"))
        assertTrue(str.contains("Field error"))
    }

    @Test
    @DisplayName("ApiError with empty details list creates correctly")
    fun apiErrorWithEmptyDetails() {
        val apiError = ApiError(code = 1, category = 0, message = "msg", details = emptyList())
        assertEquals(0, apiError.details?.size)
    }

    @Test
    @DisplayName("TokenSecretError stores message correctly")
    fun tokenSecretErrorMessage() {
        val error = TokenSecretError("Invalid token")
        assertEquals("Invalid token", error.message)
    }

    @Test
    @DisplayName("TokenSecretError with null message creates correctly")
    fun tokenSecretErrorNullMessage() {
        val error = TokenSecretError(null)
        assertEquals(null, error.message)
    }
}
