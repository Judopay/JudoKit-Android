package com.judopay.judokit.android.api.factory

import android.content.Context
import com.google.gson.GsonBuilder
import com.judopay.judokit.android.api.DsCdnApiService
import com.judopay.judokit.android.api.interceptor.NetworkConnectivityInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val CDN_BASE_URL = "https://ds-certs-cdn.vercel.app"
private const val TIMEOUT_SECONDS = 10L

internal object DsCdnApiServiceFactory {
    fun create(context: Context): DsCdnApiService {
        val builder =
            OkHttpClient
                .Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(NetworkConnectivityInterceptor(context))

        // Host-supplied debug interceptors shared across all judo SDK HTTP clients.
        builder.interceptors().addAll(JudoHttpInterceptors.interceptors)

        val client = builder.build()

        return Retrofit
            .Builder()
            .baseUrl(CDN_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(DsCdnApiService::class.java)
    }
}
