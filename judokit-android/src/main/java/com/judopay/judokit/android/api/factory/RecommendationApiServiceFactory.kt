package com.judopay.judokit.android.api.factory

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.RecommendationApiService
import com.judopay.judokit.android.ui.common.RECOMMENDATION_API_DEFAULT_TIMEOUT_SECONDS
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Factory that provides the [RecommendationApiService] used for performing all HTTP requests to the
 * Recommendation APIs. As implementation of the ApiService requires some configuration, it is better
 * to use a shared instance than create a new instance per request, so this class ensures that only
 * one instance is used in the application.
 */
object RecommendationApiServiceFactory: ApiServiceFactory() {

    private const val LOCALHOST_URL = "http://localhost/"

    override val gson: Gson
        get() = GsonBuilder().create()

    override var externalInterceptors: List<Interceptor>? = null

    /**
     * @param context the calling Context
     * @param judo the judo instance
     * @return the Retrofit API service implementation containing the methods used
     * for interacting with the Recommendation REST API.
     */
    override fun create(context: Context, judo: Judo): RecommendationApiService {
        return createRetrofit(
            context.applicationContext,
            judo,
            // This base URL is never used later on, but is required by Retrofit to be provided.
            LOCALHOST_URL
        ).create(RecommendationApiService::class.java)
    }

    override fun getOkHttpClient(
        context: Context,
        judo: Judo
    ): OkHttpClient {
        return try {
            val builder = OkHttpClient.Builder()
            setRecommendationCallTimeout(builder, judo)
            addInterceptors(builder, context, judo)
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun setRecommendationCallTimeout(builder: OkHttpClient.Builder, judo: Judo) {
        val timeout = judo.recommendationConfiguration?.recommendationTimeout ?: RECOMMENDATION_API_DEFAULT_TIMEOUT_SECONDS
        builder.callTimeout(timeout.toLong(), TimeUnit.SECONDS)
    }
}
