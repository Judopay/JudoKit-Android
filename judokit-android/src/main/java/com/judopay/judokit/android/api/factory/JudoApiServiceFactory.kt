package com.judopay.judokit.android.api.factory

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.AppMetaDataProvider
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.deserializer.ChallengeRequestIndicatorSerializer
import com.judopay.judokit.android.api.deserializer.DateJsonDeserializer
import com.judopay.judokit.android.api.deserializer.FormattedBigDecimalDeserializer
import com.judopay.judokit.android.api.deserializer.ScaExemptionSerializer
import com.judopay.judokit.android.api.interceptor.ApiHeadersInterceptor
import com.judopay.judokit.android.api.interceptor.DeviceDnaInterceptor
import com.judopay.judokit.android.api.interceptor.PayLoadInterceptor
import com.judopay.judokit.android.apiBaseUrl
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.NetworkTimeout
import com.judopay.judokit.android.model.ScaExemption
import okhttp3.OkHttpClient
import java.math.BigDecimal
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Factory that provides the [JudoApiService] used for performing all HTTP requests to the
 * JudoPay APIs. As implementation of the ApiService requires some configuration, it is better
 * to use a shared instance than create a new instance per request, so this class ensures that only
 * one instance is used in the application.
 */
@Suppress("TooGenericExceptionCaught", "TooGenericExceptionCaught", "TooGenericExceptionThrown")
object JudoApiServiceFactory : ServiceFactory<JudoApiService>() {
    override val gson: Gson
        get() =
            GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateJsonDeserializer())
                .registerTypeAdapter(BigDecimal::class.java, FormattedBigDecimalDeserializer())
                .registerTypeAdapter(ScaExemption::class.java, ScaExemptionSerializer())
                .registerTypeAdapter(
                    ChallengeRequestIndicator::class.java,
                    ChallengeRequestIndicatorSerializer(),
                ).create()

    /**
     * @param context the calling Context
     * @param judo the judo instance
     * @return the Retrofit API service implementation containing the methods used
     * for interacting with the JudoPay REST API.
     */

    @Deprecated("Use create instead", replaceWith = ReplaceWith("create(context, judo)"))
    override fun createApiService(
        context: Context,
        judo: Judo,
    ): JudoApiService = create(context, judo)

    override fun create(
        context: Context,
        judo: Judo,
    ): JudoApiService =
        createRetrofit(context.applicationContext, judo, judo.apiBaseUrl)
            .create(JudoApiService::class.java)

    override fun getOkHttpClient(
        context: Context,
        judo: Judo,
    ): OkHttpClient =
        try {
            val builder = JudoTlsConfigurator.applyTls12WithPinning(OkHttpClient.Builder())

            setTimeouts(builder, judo.networkTimeout)
            addInterceptors(builder, context, judo)
            addExternalInterceptors(builder)
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    override fun addInterceptors(
        client: OkHttpClient.Builder,
        context: Context,
        judo: Judo,
    ) {
        super.addInterceptors(client, context, judo)
        client.interceptors().apply {
            add(
                ApiHeadersInterceptor(
                    judo.authorization,
                    AppMetaDataProvider(context, judo.subProductInfo),
                ),
            )
            add(DeviceDnaInterceptor(context))
            add(PayLoadInterceptor(context))
        }
    }

    private fun setTimeouts(
        builder: OkHttpClient.Builder,
        networkTimeout: NetworkTimeout,
    ) {
        with(networkTimeout) {
            builder
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
        }
    }
}
