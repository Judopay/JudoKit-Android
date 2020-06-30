package com.judokit.android.api.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing BasicAuthorization class")
internal class BasicAuthorizationTest {

    @DisplayName("Given token is empty, then throw IllegalArgumentException")
    @Test
    fun shouldThrowIllegalArgumentExceptionIfTokenEmpty() {
        assertThrows<IllegalArgumentException> { BasicAuthorization("", "apiSecret") }
    }

    @DisplayName("Given secret is empty, then throw IllegalArgumentException")
    @Test
    fun shouldThrowIllegalArgumentExceptionIfSecretEmpty() {
        assertThrows<IllegalArgumentException> { BasicAuthorization("apiToken", "") }
    }
}
