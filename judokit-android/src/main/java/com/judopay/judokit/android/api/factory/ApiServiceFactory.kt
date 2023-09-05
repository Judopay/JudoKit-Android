package com.judopay.judokit.android.api.factory

import android.content.Context
import com.google.gson.Gson
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.ApiService
import com.judopay.judokit.android.api.AppMetaDataProvider
import com.judopay.judokit.android.api.interceptor.ApiHeadersInterceptor
import com.judopay.judokit.android.api.interceptor.NetworkConnectivityInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Abstract, base class for ApiService factories.
 */
abstract class ApiServiceFactory {

    abstract val gson: Gson

    abstract var externalInterceptors: List<Interceptor>?

    abstract fun create(context: Context, judo: Judo): ApiService

    protected fun createRetrofit(
        context: Context,
        judo: Judo,
        baseUrl: String?
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(getOkHttpClient(context, judo))
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(ApiCallAdapterFactory())
        .build()

    private val gsonConverterFactory: GsonConverterFactory
        get() = GsonConverterFactory.create(gson)

    abstract fun getOkHttpClient(
        context: Context,
        judo: Judo
    ): OkHttpClient

    open fun addInterceptors(
        client: OkHttpClient.Builder,
        context: Context,
        judo: Judo
    ) {
        client.interceptors().apply {
            add(NetworkConnectivityInterceptor(context))
            add(
                ApiHeadersInterceptor(
                    judo.authorization,
                    AppMetaDataProvider(context, judo.subProductInfo)
                )
            )
            externalInterceptors?.forEach {
                add(it)
            }
        }
    }
}
