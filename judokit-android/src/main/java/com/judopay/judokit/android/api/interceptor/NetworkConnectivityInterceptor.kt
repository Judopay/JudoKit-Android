package com.judopay.judokit.android.api.interceptor

import android.content.Context
import com.judopay.judokit.android.api.exception.NetworkConnectivityException
import com.judopay.judokit.android.ui.common.isInternetAvailable
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectivityInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable(context))
            throw NetworkConnectivityException()
        return chain.proceed(chain.request())
    }
}
