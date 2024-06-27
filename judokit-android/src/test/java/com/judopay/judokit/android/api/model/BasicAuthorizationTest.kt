package com.judopay.judokit.android.api.model

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import okhttp3.Headers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.charset.StandardCharsets

@DisplayName("Testing BasicAuthorization class")
@Deprecated(
    "This authentication method is deprecated. This test should be removed along " +
        "with BasicAuthorisation class soon.",
)
internal class BasicAuthorizationTest {
    @BeforeEach
    internal fun setUp() {
        mockkStatic("android.util.Base64")

        every {
            Base64.encodeToString(
                "token:secret".toByteArray(StandardCharsets.UTF_8),
                Base64.NO_WRAP,
            )
        } returns "credentials"
    }

    @Suppress("DEPRECATION") // The test has been set as 'deprecated' (no need for this warning).
    private val sut =
        BasicAuthorization.Builder()
            .setApiToken("token")
            .setApiSecret("secret")

    @DisplayName("Given token is empty, then throw IllegalArgumentException")
    @Test
    fun shouldThrowIllegalArgumentExceptionIfTokenEmpty() {
        assertThrows<IllegalArgumentException> { sut.setApiToken("").build() }
    }

    @DisplayName("Given token is null, then throw IllegalArgumentException")
    @Test
    fun shouldThrowIllegalArgumentExceptionIfTokenNull() {
        assertThrows<IllegalArgumentException> { sut.setApiToken(null).build() }
    }

    @DisplayName("Given secret is empty, then throw IllegalArgumentException")
    @Test
    fun shouldThrowIllegalArgumentExceptionIfSecretEmpty() {
        assertThrows<IllegalArgumentException> { sut.setApiSecret("").build() }
    }

    @DisplayName("Given secret is null, then throw IllegalArgumentException")
    @Test
    fun shouldThrowIllegalArgumentExceptionIfSecretNull() {
        assertThrows<IllegalArgumentException> { sut.setApiSecret(null).build() }
    }

    @DisplayName("Given token and secret are present, then headers should contain authorization")
    @Test
    fun headersShouldContainAuthorization() {
        val credentials =
            Base64.encodeToString(
                "token:secret".toByteArray(StandardCharsets.UTF_8),
                Base64.NO_WRAP,
            )

        val expected =
            Headers.Builder().add("Authorization", "Basic $credentials").build()
                .get("Authorization")

        assertEquals(expected, sut.build().headers.get("Authorization"))
    }
}
