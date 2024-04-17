package com.judopay.judokit.android.api.interceptor

import com.judopay.judokit.android.api.error.DuplicateTransactionError
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeDuplicationInterceptorTest {
    private val sut = DeDuplicationInterceptor()

    @DisplayName("Given intercept is called twice with same data, then throw DuplicateTransactionException")
    @Test
    fun shouldThrowDuplicateTransactionExceptionWhenDuplicate() {
        val mediaType = "application/json".toMediaTypeOrNull()

        val json =
            "{\"yourPaymentReference\": \"uniqueRef\", \"uniqueRequest\": true}"
        val body = json.toRequestBody(mediaType)

        val request =
            Request.Builder()
                .url("http://www.judopay.com")
                .post(body)
                .build()

        val responseBody = "".toResponseBody(mediaType)
        val response =
            Response.Builder()
                .request(request)
                .body(responseBody)
                .code(200)
                .message("")
                .protocol(Protocol.HTTP_1_1)
                .build()

        val chain: Interceptor.Chain = mockk(relaxed = true)

        every { chain.request() } returns request
        every { chain.proceed(request) } returns response

        sut.intercept(chain)

        assertThrows<DuplicateTransactionError> { sut.intercept(chain) }
    }

    @DisplayName("Given request body not json, then proceed request")
    @Test
    fun shouldProcessWhenRequestBodyNotJson() {
        val textHtmlMediaType = "text/html".toMediaTypeOrNull()
        val body = "".toRequestBody(textHtmlMediaType)
        val request =
            Request.Builder()
                .url("http://www.judopay.com")
                .post(body)
                .build()

        val mediaType = "application/json".toMediaTypeOrNull()
        val responseBody = "".toResponseBody(mediaType)
        val response =
            Response.Builder()
                .request(request)
                .body(responseBody)
                .code(200)
                .message("")
                .protocol(Protocol.HTTP_1_1)
                .build()

        val chain: Interceptor.Chain = mockk(relaxed = true)

        every { chain.request() } returns request

        every { chain.proceed(request) } returns response

        sut.intercept(chain)

        verify(exactly = 1) { chain.proceed(request) }
    }

    @DisplayName("Given request body null, then should proceed request")
    @Test
    fun shouldProceedWhenRequestBodyNull() {
        val request =
            Request.Builder()
                .url("http://www.judopay.com")
                .build()

        val mediaType = "application/json".toMediaTypeOrNull()
        val responseBody = "".toResponseBody(mediaType)
        val response =
            Response.Builder()
                .request(request)
                .body(responseBody)
                .code(200)
                .message("")
                .protocol(Protocol.HTTP_1_1)
                .build()

        val chain: Interceptor.Chain = mockk(relaxed = true)

        every { chain.request() } returns request

        every { chain.proceed(request) } returns response

        sut.intercept(chain)

        verify(exactly = 1) { chain.proceed(request) }
    }
}
