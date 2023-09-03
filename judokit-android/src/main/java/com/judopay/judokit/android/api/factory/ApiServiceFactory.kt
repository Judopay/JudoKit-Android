package com.judopay.judokit.android.api.factory

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.ApiService
import com.judopay.judokit.android.api.AppMetaDataProvider
import com.judopay.judokit.android.api.deserializer.ChallengeRequestIndicatorSerializer
import com.judopay.judokit.android.api.deserializer.DateJsonDeserializer
import com.judopay.judokit.android.api.deserializer.FormattedBigDecimalDeserializer
import com.judopay.judokit.android.api.deserializer.ScaExemptionSerializer
import com.judopay.judokit.android.api.interceptor.ApiHeadersInterceptor
import com.judopay.judokit.android.api.interceptor.DeDuplicationInterceptor
import com.judopay.judokit.android.api.interceptor.DeviceDnaInterceptor
import com.judopay.judokit.android.api.interceptor.NetworkConnectivityInterceptor
import com.judopay.judokit.android.api.interceptor.PayLoadInterceptor
import com.judopay.judokit.android.model.ChallengeRequestIndicator
import com.judopay.judokit.android.model.NetworkTimeout
import com.judopay.judokit.android.model.ScaExemption
import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.security.KeyStore
import java.util.Arrays
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

private const val HOSTNAME_WILDCARD_PATTERN = "*.judopay.com"

/**
 * Abstract, base class for ApiService factories.
 */
abstract class ApiServiceFactory {

    companion object {
        val gson: Gson
            get() = GsonBuilder()
                .registerTypeAdapter(Date::class.java, DateJsonDeserializer())
                .registerTypeAdapter(BigDecimal::class.java, FormattedBigDecimalDeserializer())
                .registerTypeAdapter(ScaExemption::class.java, ScaExemptionSerializer())
                .registerTypeAdapter(
                    ChallengeRequestIndicator::class.java,
                    ChallengeRequestIndicatorSerializer()
                )
                .create()
    }

    var externalInterceptors: List<Interceptor>? = null

    abstract fun create(context: Context, judo: Judo): ApiService

    protected fun createRetrofit(
        context: Context,
        judo: Judo,
        baseUrl: String?
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(getOkHttpClient(context, judo))
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(JudoApiCallAdapterFactory())
        .build()

    private val gsonConverterFactory: GsonConverterFactory
        get() = GsonConverterFactory.create(gson)

    private fun getOkHttpClient(
        context: Context,
        judo: Judo
    ): OkHttpClient {
        return try {
            val sslContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)

            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)

            val trustManagers = trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers.first() !is X509TrustManager)) {
                "Unexpected default trust managers: " + Arrays.toString(
                    trustManagers
                )
            }

            val trustManager = trustManagers.first() as X509TrustManager
            val specs: MutableList<ConnectionSpec> = ArrayList()

            specs.add(
                ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build()
            )

            val builder = OkHttpClient.Builder()
                .sslSocketFactory(Tls12SslSocketFactory(sslContext.socketFactory), trustManager)
                .connectionSpecs(specs)

            builder.certificatePinner(
                CertificatePinner.Builder()
                    .add(
                        HOSTNAME_WILDCARD_PATTERN,
                        "sha256/SuY75QgkSNBlMtHNPeW9AayE7KNDAypMBHlJH9GEhXs=",
                        "sha256/c4zbAoMygSbepJKqU3322FvFv5unm+TWZROW3FHU1o8="
                    )
                    .build()
            )

            setTimeouts(builder, judo.networkTimeout)
            // Todo: Recommendation timeout: first clarify with Stefan how to consider Recommendation timeout (what about readTimeout, writeTimeout).
            // val timeout = judo.recommendationConfiguration?.recommendationTimeout ?: RECOMMENDATION_API_DEFAULT_TIMEOUT_SECONDS
            // setRecommendationCustomTimeout(builder, timeout.toLong())
            addInterceptors(builder, context, judo)
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun setTimeouts(builder: OkHttpClient.Builder, networkTimeout: NetworkTimeout) {
        with(networkTimeout) {
            builder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
        }
    }

    private fun setRecommendationCustomTimeout(builder: OkHttpClient.Builder, recommendationApiTimeout: Long) {
        builder.connectTimeout(recommendationApiTimeout, TimeUnit.SECONDS)
    }

    private fun addInterceptors(
        client: OkHttpClient.Builder,
        context: Context,
        judo: Judo
    ) = client.interceptors().apply {
        add(NetworkConnectivityInterceptor(context))
        add(DeDuplicationInterceptor())
        add(DeviceDnaInterceptor(context))
        add(
            ApiHeadersInterceptor(
                judo.authorization,
                AppMetaDataProvider(context, judo.subProductInfo)
            )
        )
        add(PayLoadInterceptor(context))

        externalInterceptors?.forEach {
            add(it)
        }
    }
}