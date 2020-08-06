package com.judokit.android.api.interceptor

import android.content.Context
import android.os.Build
import com.judokit.android.BuildConfig
import com.judokit.android.api.AppMetaDataProvider
import com.judokit.android.api.model.Authorization
import java.io.IOException
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val ACCEPT_HEADER = "Accept"
private const val API_VERSION_HEADER = "Api-Version"
private const val CACHE_CONTROL_HEADER = "Cache-Control"
private const val UI_MODE_HEADER = "UI-Client-Mode"
private const val SDK_VERSION_HEADER = "Sdk-Version"
private const val USER_AGENT_HEADER = "User-Agent"
private const val JSON_MIME_TYPE = "application/json"
private const val API_VERSION = "5.6.0"
private const val CACHE_CONTROL = "no-cache"
private const val CUSTOM_UI_MODE = "Custom-UI"

internal class ApiHeadersInterceptor(
    private val authorization: Authorization,
    context: Context
) : Interceptor {

    private val appMetaDataProvider = AppMetaDataProvider(context)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .headers(headers)
            .build()
        return chain.proceed(request)
    }

    private val headers: Headers
        get() = Headers.Builder()
            .addAll(authorization.headers)
            .add(CONTENT_TYPE_HEADER, JSON_MIME_TYPE)
            .add(ACCEPT_HEADER, JSON_MIME_TYPE)
            .add(API_VERSION_HEADER, API_VERSION)
            .add(CACHE_CONTROL_HEADER, CACHE_CONTROL)
            .add(SDK_VERSION_HEADER, "Android-" + BuildConfig.VERSION_NAME)
            .addUnsafeNonAscii(USER_AGENT_HEADER, userAgent)
            .add(UI_MODE_HEADER, CUSTOM_UI_MODE)
            .build()

    private val userAgent: String
        get() =
            """Android/${BuildConfig.VERSION_NAME} ${Build.MANUFACTURER} ${Build.MODEL} ${appMetaDataProvider.appName} ${appMetaDataProvider.appVersion}""".trimMargin()
}
