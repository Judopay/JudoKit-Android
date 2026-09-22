package com.judopay.judokit.android.api.interceptor

import com.judopay.judokit.android.api.AppMetaDataProvider
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private const val USER_AGENT_HEADER = "User-Agent"

/**
 * Sets the judo SDK [AppMetaDataProvider.userAgent] on requests that would otherwise be sent
 * with OkHttp's default `User-Agent` — notably the DS-certificate CDN client, which does not
 * run through [ApiHeadersInterceptor]. All other headers on the request are left untouched.
 */
internal class UserAgentInterceptor(
    private val appMetaDataProvider: AppMetaDataProvider,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val headers =
            original.headers
                .newBuilder()
                .removeAll(USER_AGENT_HEADER)
                .addUnsafeNonAscii(USER_AGENT_HEADER, appMetaDataProvider.userAgent)
                .build()
        return chain.proceed(original.newBuilder().headers(headers).build())
    }
}
