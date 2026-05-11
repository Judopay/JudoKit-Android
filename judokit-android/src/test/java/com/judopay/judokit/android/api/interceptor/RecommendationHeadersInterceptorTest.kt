package com.judopay.judokit.android.api.interceptor

import com.judopay.judokit.android.api.model.Authorization
import com.judopay.judokit.android.api.model.PaymentSessionAuthorization
import io.mockk.every
import io.mockk.mockk
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing RecommendationHeadersInterceptor")
internal class RecommendationHeadersInterceptorTest {
    private val mockWebServer = MockWebServer()

    @BeforeEach
    fun setUp() {
        mockWebServer.start()
        mockWebServer.enqueue(MockResponse())
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    private fun buildClient(authorization: Authorization): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(RecommendationHeadersInterceptor(authorization))
            .build()

    private fun authWithSession(session: String): Authorization {
        val authorization =
            PaymentSessionAuthorization
                .Builder()
                .setApiToken("api-token")
                .setPaymentSession(session)
                .build()
        return authorization
    }

    private fun authWithoutSession(): Authorization {
        val mockAuth = mockk<Authorization>()
        every { mockAuth.headers } returns Headers.Builder().build()
        return mockAuth
    }

    private fun executeRequest(client: OkHttpClient): okhttp3.mockwebserver.RecordedRequest {
        val request =
            Request
                .Builder()
                .url(mockWebServer.url("/recommendation"))
                .get()
                .build()
        client.newCall(request).execute()
        return mockWebServer.takeRequest()
    }

    @Test
    @DisplayName("Interceptor sets Content-Type header to application/json")
    fun setsContentTypeHeader() {
        val recorded = executeRequest(buildClient(authWithSession("session-token")))
        assertEquals("application/json", recorded.getHeader("Content-Type"))
    }

    @Test
    @DisplayName("Interceptor sets Accept header to application/json")
    fun setsAcceptHeader() {
        val recorded = executeRequest(buildClient(authWithSession("session-token")))
        assertEquals("application/json", recorded.getHeader("Accept"))
    }

    @Test
    @DisplayName("Interceptor sets Cache-Control header to no-cache")
    fun setsCacheControlHeader() {
        val recorded = executeRequest(buildClient(authWithSession("session-token")))
        assertEquals("no-cache", recorded.getHeader("Cache-Control"))
    }

    @Test
    @DisplayName("Interceptor sets payment-session header from authorization")
    fun setsPaymentSessionHeader() {
        val recorded = executeRequest(buildClient(authWithSession("my-payment-session")))
        assertEquals("my-payment-session", recorded.getHeader("payment-session"))
    }

    @Test
    @DisplayName("Interceptor sets empty payment-session header when authorization has no session")
    fun setsEmptyPaymentSessionWhenMissing() {
        mockWebServer.enqueue(MockResponse())
        val recorded = executeRequest(buildClient(authWithoutSession()))
        assertEquals("", recorded.getHeader("payment-session"))
    }
}
