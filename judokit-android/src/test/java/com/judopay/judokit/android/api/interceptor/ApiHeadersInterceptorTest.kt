package com.judopay.judokit.android.api.interceptor

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import com.judopay.judokit.android.api.AppMetaDataProvider
import com.judopay.judokit.android.api.model.BasicAuthorization
import com.judopay.judokit.android.model.SubProductInfo
import com.judopay.judokit.android.ui.common.JUDO_API_VERSION
import com.judopay.judokit.android.ui.common.JUDO_KIT_VERSION
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
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
    private val authorization = BasicAuthorization.Builder().setApiToken("token").setApiSecret("secret")
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

        mockkObject(AppMetaDataProvider.SystemInfo)
        every { AppMetaDataProvider.SystemInfo.androidVersionString } returns "13.0"
        every { AppMetaDataProvider.SystemInfo.deviceManufacturer } returns "Google"
        every { AppMetaDataProvider.SystemInfo.deviceModel } returns "Pixel 7"

        val sut = ApiHeadersInterceptor(authorization.build(), createAppMetadataProvider())
        okHttpClient.addInterceptor(sut)
    }

    private fun createAppMetadataProvider(subProductInfo: SubProductInfo = SubProductInfo.Unknown): AppMetaDataProvider {
        val packageManagerMock = mockk<PackageManager>(relaxed = true)
        every { packageManagerMock.getApplicationLabel(any()) } returns "Test application"
        every { packageManagerMock.getPackageInfo(any<String>(), 0) } returns PackageInfo().apply { this.versionName = "1.0" }
        val mockContext = mockk<Context>(relaxed = true)
        every { mockContext.applicationContext.packageManager } returns packageManagerMock
        return AppMetaDataProvider(mockContext, subProductInfo)
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
    fun addJudoApiVersionHeader() {
        val recordedRequest = makeRequest("/")

        assertEquals(
            JUDO_API_VERSION,
            recordedRequest.getHeader("Api-Version")
        )
    }

    @DisplayName("Given request is intercepted, when resource is bank, then add api version header")
    @Test
    fun addBankApiVersionHeader() {
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

    @DisplayName("Given request is intercepted, then add user agent header with no sub product")
    @Test
    fun addUserAgentHeaderNoSubProduct() {
        val recordedRequest = makeRequest("/")

        assertEquals(
            "JudoKit-Android/$JUDO_KIT_VERSION Android/13.0 Test application/1.0 Google Pixel 7",
            recordedRequest.getHeader("User-Agent")
        )
    }

    @DisplayName("Given request is intercepted, then add user agent header with ReactNative sub product")
    @Test
    fun addUserAgentHeaderWithReactNativeSubProduct() {
        val appMetadataProvider = createAppMetadataProvider(SubProductInfo.ReactNative("4.0.0"))
        okHttpClient.interceptors().clear()
        okHttpClient.addInterceptor(ApiHeadersInterceptor(authorization.build(), appMetadataProvider))

        val recordedRequest = makeRequest("/")

        assertEquals(
            "JudoKit-Android/$JUDO_KIT_VERSION (JudoKit-ReactNative/4.0.0) Android/13.0 Test application/1.0 Google Pixel 7",
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
