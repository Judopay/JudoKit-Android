package com.judopay.judokit.android.api.model

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import okhttp3.Headers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.charset.StandardCharsets

@DisplayName("Testing PaymentSessionAuthorization class")
internal class PaymentSessionAuthorizationTest {
    private val sut =
        PaymentSessionAuthorization
            .Builder()
            .setApiToken("token")
            .setPaymentSession("paymentSession")

    @BeforeEach
    internal fun setUp() {
        mockkStatic("android.util.Base64")

        every {
            Base64.encodeToString(
                "token:".toByteArray(StandardCharsets.UTF_8),
                Base64.NO_WRAP,
            )
        } returns "credentials"
    }

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

    @DisplayName("Given paymentSession is empty, then throw IllegalArgumentException")
    @Test
    fun shouldThrowIllegalArgumentExceptionIfPaymentSessionEmpty() {
        assertThrows<IllegalArgumentException> { sut.setPaymentSession("").build() }
    }

    @DisplayName("Given paymentSession is null, then throw IllegalArgumentException")
    @Test
    fun shouldThrowIllegalArgumentExceptionIfPaymentSessionNull() {
        assertThrows<IllegalArgumentException> { sut.setPaymentSession(null).build() }
    }

    @DisplayName("Given token and paymentSession are present, then headers should contain Api-Token")
    @Test
    fun headersShouldContainAuthorization() {
        val expected = Headers.Builder().add("Api-Token", "token").build()["Api-Token"]

        Assertions.assertEquals(expected, sut.build().headers["Api-Token"])
    }

    @DisplayName("Given token and paymentSession are present, then headers should contain Payment-Session")
    @Test
    fun headersShouldContainPaymentSession() {
        val expected =
            Headers
                .Builder()
                .add("Payment-Session", "paymentSession")
                .build()
                .get("Payment-Session")

        Assertions.assertEquals(expected, sut.build().headers["Payment-Session"])
    }
}
