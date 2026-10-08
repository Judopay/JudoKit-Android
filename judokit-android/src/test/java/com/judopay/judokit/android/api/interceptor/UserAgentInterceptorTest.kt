package com.judopay.judokit.android.api.interceptor

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.judopay.judokit.android.api.AppMetaDataProvider
import com.judopay.judokit.android.model.SubProductInfo
import com.judopay.judokit.android.ui.common.JUDO_KIT_VERSION
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
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

internal class UserAgentInterceptorTest {
    private val okHttpClient = OkHttpClient.Builder()
    private val mockWebServer = MockWebServer()

    @BeforeEach
    internal fun setUp() {
        mockWebServer.start()
        mockWebServer.enqueue(MockResponse())

        mockkObject(AppMetaDataProvider.SystemInfo)
        every { AppMetaDataProvider.SystemInfo.androidVersionString } returns "13.0"
        every { AppMetaDataProvider.SystemInfo.deviceManufacturer } returns "Google"
        every { AppMetaDataProvider.SystemInfo.deviceModel } returns "Pixel 7"

        okHttpClient.addInterceptor(UserAgentInterceptor(createAppMetadataProvider()))
    }

    @AfterEach
    internal fun tearDown() {
        mockWebServer.shutdown()
    }

    @DisplayName("Given request is intercepted, then set the judo SDK user agent header")
    @Test
    fun setsJudoUserAgentHeader() {
        val recordedRequest = makeRequest()

        assertEquals(
            "JudoKit-Android/$JUDO_KIT_VERSION Android/13.0 Test application/1.0 Google Pixel 7",
            recordedRequest.getHeader("User-Agent"),
        )
    }

    @DisplayName("Given the request already carries a user agent, then it is replaced")
    @Test
    fun replacesExistingUserAgentHeader() {
        val recordedRequest = makeRequest(userAgent = "okhttp/4.12.0")

        assertEquals(
            "JudoKit-Android/$JUDO_KIT_VERSION Android/13.0 Test application/1.0 Google Pixel 7",
            recordedRequest.getHeader("User-Agent"),
        )
    }

    private fun makeRequest(userAgent: String? = null): RecordedRequest {
        val request =
            Request
                .Builder()
                .url(mockWebServer.url("/"))
                .apply { userAgent?.let { header("User-Agent", it) } }
                .build()
        okHttpClient.build().newCall(request).execute()
        return mockWebServer.takeRequest()
    }

    private fun createAppMetadataProvider(subProductInfo: SubProductInfo = SubProductInfo.Unknown): AppMetaDataProvider {
        val packageManagerMock = mockk<PackageManager>(relaxed = true)
        every { packageManagerMock.getApplicationLabel(any()) } returns "Test application"
        every { packageManagerMock.getPackageInfo(any<String>(), 0) } returns
            PackageInfo().apply { versionName = "1.0" }
        val mockContext = mockk<Context>(relaxed = true)
        every { mockContext.applicationContext.packageManager } returns packageManagerMock
        return AppMetaDataProvider(mockContext, subProductInfo)
    }
}
