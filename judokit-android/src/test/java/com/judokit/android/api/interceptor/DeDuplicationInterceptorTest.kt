package com.judokit.android.api.interceptor

import com.judokit.android.api.error.DuplicateTransactionError
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeDuplicationInterceptorTest {

    private val sut = DeDuplicationInterceptor()

    @DisplayName("Given intercept is called twice with same data, then throw DuplicateTransactionException")
    @Test
    fun shouldThrowDuplicateTransactionExceptionWhenDuplicate() {

        val mediaType = MediaType.parse("application/json")

        val json =
            "{\"yourPaymentReference\": \"uniqueRef\", \"uniqueRequest\": true}"
        val body = RequestBody.create(mediaType, json)

        val request = Request.Builder()
            .url("http://www.judopay.com")
            .post(body)
            .build()

        val responseBody = ResponseBody.create(mediaType, "")
        val response = Response.Builder()
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
        val textHtmlMediaType = MediaType.parse("text/html")
        val body = RequestBody.create(textHtmlMediaType, "")
        val request = Request.Builder()
            .url("http://www.judopay.com")
            .post(body)
            .build()

        val mediaType = MediaType.parse("application/json")
        val responseBody = ResponseBody.create(mediaType, "")
        val response = Response.Builder()
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
        val request = Request.Builder()
            .url("http://www.judopay.com")
            .build()

        val mediaType = MediaType.parse("application/json")
        val responseBody = ResponseBody.create(mediaType, "")
        val response = Response.Builder()
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
