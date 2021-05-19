package com.judopay.judokit.android.api.factory

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.AppMetaDataProvider
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.deserializer.DateJsonDeserializer
import com.judopay.judokit.android.api.deserializer.FormattedBigDecimalDeserializer
import com.judopay.judokit.android.api.interceptor.ApiHeadersInterceptor
import com.judopay.judokit.android.api.interceptor.DeDuplicationInterceptor
import com.judopay.judokit.android.api.interceptor.DeviceDnaInterceptor
import com.judopay.judokit.android.api.interceptor.NetworkConnectivityInterceptor
import com.judopay.judokit.android.api.interceptor.PayLoadInterceptor
import com.judopay.judokit.android.apiBaseUrl
import com.judopay.judokit.android.model.NetworkTimeout
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
 * Factory that provides the [JudoApiService] used for performing all HTTP requests to the
 * judoPay APIs. As creating the JudoApiService requires lots of setup, it is better to use a shared
 * instance than create a new instance per request, so this class ensures that only one instance is
 * used in the application.
 */
object JudoApiServiceFactory {

    /**
     * @param context the calling Context
     * @param judo the judo instance
     * @return the Retrofit API service implementation containing the methods used
     * for interacting with the judoPay REST API.
     */
    @JvmStatic
    fun createApiService(context: Context, judo: Judo): JudoApiService =
        createRetrofit(context.applicationContext, judo).create(JudoApiService::class.java)

    @JvmStatic
    var externalInterceptors: List<Interceptor>? = null

    private fun createRetrofit(context: Context, judo: Judo): Retrofit = Retrofit.Builder()
        .baseUrl(judo.apiBaseUrl)
        .client(getOkHttpClient(context, judo))
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(JudoApiCallAdapterFactory())
        .build()

    private val gsonConverterFactory: GsonConverterFactory
        get() = GsonConverterFactory.create(gson)

    @JvmStatic
    val gson: Gson
        get() = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateJsonDeserializer())
            .registerTypeAdapter(BigDecimal::class.java, FormattedBigDecimalDeserializer())
            .create()

    private fun getOkHttpClient(context: Context, judo: Judo): OkHttpClient {
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

    private fun addInterceptors(
        client: OkHttpClient.Builder,
        context: Context,
        judo: Judo
    ) = client.interceptors().apply {

        add(NetworkConnectivityInterceptor(context))
        add(DeDuplicationInterceptor())
        add(DeviceDnaInterceptor(context))
        add(ApiHeadersInterceptor(judo.authorization, AppMetaDataProvider(context)))
        add(PayLoadInterceptor(context))

        externalInterceptors?.forEach {
            add(it)
        }
    }
}
