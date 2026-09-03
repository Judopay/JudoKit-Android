package com.judopay.judokit.android.api.interceptor

import android.content.Context
import com.google.gson.JsonParser
import com.judopay.judokit.android.api.DeviceDetailsProvider
import com.judopay.judokit.android.api.model.request.DeviceDetails
import io.mockk.every
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.BufferedSink
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class DeviceDnaInterceptorTest {
    private val okHttpClient = OkHttpClient.Builder()
    private val mockWebServer = MockWebServer()
    private val deviceDetailsProvider = mockk<DeviceDetailsProvider>()

    @BeforeEach
    internal fun setUp() {
        mockWebServer.start()
        mockWebServer.enqueue(MockResponse())

        every { deviceDetailsProvider.deviceDetails } returns
            DeviceDetails(
                kDeviceId = "k-device-id",
                vDeviceId = "v-device-id",
                countryCode = "GB",
                cultureLocale = "en_GB",
                os = "Android 15",
            )

        val sut = DeviceDnaInterceptor(mockk<Context>(relaxed = true), deviceDetailsProvider)
        okHttpClient.addInterceptor(sut)
    }

    @AfterEach
    internal fun tearDown() {
        mockWebServer.shutdown()
    }

    @DisplayName("Given a POST request with a JSON body, then add the deviceDetails object")
    @Test
    fun addDeviceDetailsToPostRequests() {
        val recordedRequest = makePostRequest("""{"amount":"1.50"}""")

        val body = JsonParser.parseString(recordedRequest.body.readUtf8()).asJsonObject
        val deviceDetails = body.getAsJsonObject("deviceDetails")

        assertEquals("1.50", body.get("amount").asString)
        assertEquals("k-device-id", deviceDetails.get("kDeviceId").asString)
        assertEquals("v-device-id", deviceDetails.get("vDeviceId").asString)
        assertEquals("GB", deviceDetails.get("countryCode").asString)
        assertEquals("en_GB", deviceDetails.get("cultureLocale").asString)
        assertEquals("Android 15", deviceDetails.get("os").asString)
    }

    @DisplayName("Given the body has a charset, then the rewritten body keeps the original content type")
    @Test
    fun preserveOriginalContentType() {
        val recordedRequest =
            makePostRequest("""{"amount":"1.50"}""", contentType = "application/json; charset=utf-8")

        assertEquals("application/json; charset=utf-8", recordedRequest.getHeader("Content-Type"))

        val body = JsonParser.parseString(recordedRequest.body.readUtf8()).asJsonObject
        assertEquals("k-device-id", body.getAsJsonObject("deviceDetails").get("kDeviceId").asString)
    }

    @DisplayName("Given the body already contains deviceDetails, then leave the body untouched")
    @Test
    fun skipWhenDeviceDetailsPresent() {
        val originalBody = """{"deviceDetails":{"kDeviceId":"existing"}}"""
        val recordedRequest = makePostRequest(originalBody)

        val body = JsonParser.parseString(recordedRequest.body.readUtf8()).asJsonObject

        assertEquals("existing", body.getAsJsonObject("deviceDetails").get("kDeviceId").asString)
    }

    @DisplayName("Given the body is not valid JSON, then leave the body untouched")
    @Test
    fun skipWhenBodyIsNotJson() {
        val recordedRequest = makePostRequest("not-json")

        assertEquals("not-json", recordedRequest.body.readUtf8())
    }

    @DisplayName("Given the content type is not JSON, then leave the body untouched")
    @Test
    fun skipWhenContentTypeIsNotJson() {
        val recordedRequest = makePostRequest("""{"amount":"1.50"}""", contentType = "text/plain")

        assertEquals("""{"amount":"1.50"}""", recordedRequest.body.readUtf8())
    }

    @DisplayName("Given the body is one-shot, then leave the body untouched")
    @Test
    fun skipOneShotBodies() {
        val oneShotBody =
            object : RequestBody() {
                override fun contentType() = "application/json".toMediaType()

                override fun isOneShot() = true

                override fun writeTo(sink: BufferedSink) {
                    sink.writeUtf8("""{"amount":"1.50"}""")
                }
            }

        val recordedRequest = makePostRequest(oneShotBody)

        assertEquals("""{"amount":"1.50"}""", recordedRequest.body.readUtf8())
    }

    @DisplayName("Given the request is not a POST, then leave the request untouched")
    @Test
    fun skipNonPostRequests() {
        okHttpClient
            .build()
            .newCall(
                Request
                    .Builder()
                    .url(mockWebServer.url("/"))
                    .get()
                    .build(),
            ).execute()

        val recordedRequest = mockWebServer.takeRequest()

        assertFalse(recordedRequest.body.readUtf8().contains("deviceDetails"))
    }

    private fun makePostRequest(
        body: String,
        contentType: String = "application/json",
    ): RecordedRequest = makePostRequest(body.toRequestBody(contentType.toMediaType()))

    private fun makePostRequest(body: RequestBody): RecordedRequest {
        okHttpClient
            .build()
            .newCall(
                Request
                    .Builder()
                    .url(mockWebServer.url("/"))
                    .post(body)
                    .build(),
            ).execute()

        return mockWebServer.takeRequest()
    }
}
