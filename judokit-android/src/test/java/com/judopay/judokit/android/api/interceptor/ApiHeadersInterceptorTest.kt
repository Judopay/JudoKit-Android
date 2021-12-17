package com.judopay.judokit.android.api.interceptor

import android.util.Base64
import com.judopay.judokit.android.api.AppMetaDataProvider
import com.judopay.judokit.android.api.model.BasicAuthorization
import com.judopay.judokit.android.ui.common.JUDO_KIT_VERSION
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

internal class ApiHeadersInterceptorTest {

    private val authorization =
        BasicAuthorization.Builder().setApiToken("token").setApiSecret("secret")
    private val appMetadataProvider = mockk<AppMetaDataProvider>(relaxed = true)
    private val okHttpClient = OkHttpClient.Builder()
    private val mockWebServer = MockWebServer()

    @BeforeEach
    internal fun setUp() {
        mockkStatic("android.util.Base64")
        every {
            Base64.encodeToString(
                "token:secret".toByteArray(StandardCharsets.UTF_8),
                Base64.NO_WRAP
            )
        } returns "credentials"

        mockWebServer.start()
        mockWebServer.enqueue(MockResponse())
        val sut = ApiHeadersInterceptor(authorization.build(), appMetadataProvider)
        okHttpClient.addInterceptor(sut)
    }

    @AfterEach
    internal fun tearDown() {
        mockWebServer.shutdown()
    }

    @DisplayName("Given request is intercepted, then add authorization header")
    @Test
    fun addAuthorizationHeader() {
        val recordedRequest = makeRequest("/")

        assertEquals(
            authorization.build().headers["Authorization"],
            recordedRequest.getHeader("Authorization")
        )
    }

    private fun makeRequest(url: String): RecordedRequest {
        okHttpClient.build().newCall(Request.Builder().url(mockWebServer.url(url)).build())
            .execute()
        return mockWebServer.takeRequest()
    }

    @DisplayName("Given request is intercepted, then add content type header")
    @Test
    fun addContentTypeHeader() {
        val recordedRequest = makeRequest("/")

        assertEquals(
            "application/json",
            recordedRequest.getHeader("Content-Type")
        )
    }

    @DisplayName("Given request is intercepted, then add accept header")
    @Test
    fun addAcceptHeader() {
        val recordedRequest = makeRequest("/")

        assertEquals(
            "application/json",
            recordedRequest.getHeader("Accept")
        )
    }

    @DisplayName("Given request is intercepted, when resource is transactions, then add api version header")
    @Test
    fun add560ApiVersionHeader() {
        val recordedRequest = makeRequest("/")

        assertEquals(
            "5.6.0",
            recordedRequest.getHeader("Api-Version")
        )
    }

    @DisplayName("Given request is intercepted, when resource is bank, then add api version header")
    @Test
    fun add2000ApiVersionHeader() {
        val recordedRequest = makeRequest("/order/bank/sale")

        assertEquals(
            "2.0.0.0",
            recordedRequest.getHeader("Api-Version")
        )
    }

    @DisplayName("Given request is intercepted, then add cache control header")
    @Test
    fun addCacheControlHeader() {
        val recordedRequest = makeRequest("/")

        assertEquals(
            "no-cache",
            recordedRequest.getHeader("Cache-Control")
        )
    }

    @DisplayName("Given request is intercepted, then add sdk version header")
    @Test
    fun addSdkVersionHeader() {
        val recordedRequest = makeRequest("/")

        assertEquals(
            "Android-$JUDO_KIT_VERSION",
            recordedRequest.getHeader("Sdk-Version")
        )
    }

    @DisplayName("Given request is intercepted, then add user agent header")
    @Test
    fun addUserAgentHeader() {
        every { appMetadataProvider.userAgent } returns "userAgent"

        val recordedRequest = makeRequest("/")

        assertEquals(
            appMetadataProvider.userAgent,
            recordedRequest.getHeader("User-Agent")
        )
    }

    @DisplayName("Given request is intercepted, then add ui mode header")
    @Test
    fun addUiModeHeader() {
        val recordedRequest = makeRequest("/")

        assertEquals(
            "Custom-UI",
            recordedRequest.getHeader("UI-Client-Mode")
        )
    }
}
