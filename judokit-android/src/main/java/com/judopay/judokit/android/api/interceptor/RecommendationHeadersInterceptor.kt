package com.judopay.judokit.android.api.interceptor

import com.judopay.judokit.android.api.model.Authorization
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val ACCEPT_HEADER = "Accept"
private const val CACHE_CONTROL_HEADER = "Cache-Control"
private const val JSON_MIME_TYPE = "application/json"
private const val CACHE_CONTROL = "no-cache"
private const val PAYMENT_SESSION_HEADER = "Payment-Session"

internal class RecommendationHeadersInterceptor(
    private val authorization: Authorization,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val session = authorization.headers[PAYMENT_SESSION_HEADER]

        val headers =
            Headers
                .Builder()
                .add(PAYMENT_SESSION_HEADER.lowercase(), session ?: "")
                .add(CONTENT_TYPE_HEADER, JSON_MIME_TYPE)
                .add(ACCEPT_HEADER, JSON_MIME_TYPE)
                .add(CACHE_CONTROL_HEADER, CACHE_CONTROL)
                .build()

        val request =
            chain
                .request()
                .newBuilder()
                .headers(headers)
                .build()

        return chain.proceed(request)
    }
}
