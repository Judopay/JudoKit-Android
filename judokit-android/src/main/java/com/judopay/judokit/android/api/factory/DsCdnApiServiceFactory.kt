package com.judopay.judokit.android.api.factory

import android.content.Context
import com.google.gson.GsonBuilder
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.DsCdnApiService
import com.judopay.judokit.android.api.interceptor.NetworkConnectivityInterceptor
import com.judopay.judokit.android.apiBaseUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TIMEOUT_SECONDS = 10L

@Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
internal object DsCdnApiServiceFactory {
    fun create(
        context: Context,
        judo: Judo,
    ): DsCdnApiService =
        try {
            val builder =
                JudoTlsConfigurator
                    .applyTls12WithPinning(OkHttpClient.Builder())
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .addInterceptor(NetworkConnectivityInterceptor(context))

            // Host-supplied debug interceptors shared across all judo SDK HTTP clients.
            builder.interceptors().addAll(JudoHttpInterceptors.interceptors)

            Retrofit
                .Builder()
                .baseUrl(judo.apiBaseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
                .create(DsCdnApiService::class.java)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}
