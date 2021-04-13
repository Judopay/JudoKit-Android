package com.judopay.judokit.android.api.interceptor

import com.judopay.judokit.android.BuildConfig
import com.judopay.judokit.android.api.AppMetaDataProvider
import com.judopay.judokit.android.api.model.Authorization
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val ACCEPT_HEADER = "Accept"
private const val API_VERSION_HEADER = "Api-Version"
private const val CACHE_CONTROL_HEADER = "Cache-Control"
private const val UI_MODE_HEADER = "UI-Client-Mode"
private const val SDK_VERSION_HEADER = "Sdk-Version"
private const val USER_AGENT_HEADER = "User-Agent"
private const val JSON_MIME_TYPE = "application/json"
private const val API_VERSION = "5.6.0"
private const val BANK_API_VERSION = "2.0.0.0"
private const val CACHE_CONTROL = "no-cache"
private const val CUSTOM_UI_MODE = "Custom-UI"

private const val BANK_ENDPOINT = "/order/bank"

internal class ApiHeadersInterceptor(
    private val authorization: Authorization,
    private val appMetaDataProvider: AppMetaDataProvider
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath
        val isSaleRequest = path.startsWith(BANK_ENDPOINT)
        val headers = getHeaders(isSaleRequest)
        val request = chain.request()
            .newBuilder()
            .headers(headers)
            .build()

        return chain.proceed(request)
    }

    private fun getHeaders(isSaleRequest: Boolean) = Headers.Builder()
        .addAll(authorization.headers)
        .add(CONTENT_TYPE_HEADER, JSON_MIME_TYPE)
        .add(ACCEPT_HEADER, JSON_MIME_TYPE)
        .add(API_VERSION_HEADER, if (isSaleRequest) BANK_API_VERSION else API_VERSION)
        .add(CACHE_CONTROL_HEADER, CACHE_CONTROL)
        .add(SDK_VERSION_HEADER, "Android-${BuildConfig.VERSION_NAME}")
        .addUnsafeNonAscii(USER_AGENT_HEADER, appMetaDataProvider.userAgent)
        .add(UI_MODE_HEADER, CUSTOM_UI_MODE)
        .build()
}
