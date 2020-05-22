package com.judokit.android.api.model

import com.judokit.android.api.error.TokenSecretError
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing credentials class")
internal class CredentialsTest {

    @DisplayName("Given token is empty, then throw TokenSecretError")
    @Test
    fun shouldThrowTokenSecretErrorIfTokenEmpty() {
        assertThrows<TokenSecretError> { Credentials("", "apiSecret") }
    }

    @DisplayName("Given secret is empty, then throw TokenSecretError")
    @Test
    fun shouldThrowTokenSecretErrorIfSecretEmpty() {
        assertThrows<TokenSecretError> { Credentials("apiToken", "") }
    }
}
