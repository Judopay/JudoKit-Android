package com.judopay.api.interceptor

import android.content.Context
import android.os.Build
import com.judopay.BuildConfig
import com.judopay.api.AppMetaDataProvider
import com.judopay.api.model.Credentials
import java.io.IOException
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

private const val AUTHORIZATION_HEADER = "Authorization"
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
    private val credentials: Credentials,
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
            .add(AUTHORIZATION_HEADER, credentials.basicAuthorizationHeader)
            .add(CONTENT_TYPE_HEADER, JSON_MIME_TYPE)
            .add(ACCEPT_HEADER, JSON_MIME_TYPE)
            .add(API_VERSION_HEADER, API_VERSION)
            .add(CACHE_CONTROL_HEADER, CACHE_CONTROL)
            .add(SDK_VERSION_HEADER, "Android-" + BuildConfig.VERSION_NAME)
            .addUnsafeNonAscii(USER_AGENT_HEADER, userAgent)
            .add(UI_MODE_HEADER, CUSTOM_UI_MODE)
            .build()

    private val userAgent: String
        get() = """Android/${BuildConfig.VERSION_NAME} ${Build.MANUFACTURER} ${Build.MODEL} ${appMetaDataProvider.appName} ${appMetaDataProvider.appVersion}""".trimMargin()
}
