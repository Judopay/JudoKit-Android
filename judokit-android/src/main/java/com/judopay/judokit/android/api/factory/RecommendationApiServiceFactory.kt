package com.judopay.judokit.android.api.factory

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.RecommendationApiService
import com.judopay.judokit.android.api.interceptor.RecommendationHeadersInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

private const val RECOMMENDATION_API_DEFAULT_TIMEOUT_SECONDS = 30

/**
 * Factory that provides the [RecommendationApiService] used for performing all HTTP requests to the
 * Recommendation API.
 */
object RecommendationApiServiceFactory : ServiceFactory<RecommendationApiService>() {
    private const val LOCALHOST_URL = "http://localhost/"

    override val gson: Gson
        get() = GsonBuilder().create()

    override var externalInterceptors: List<Interceptor>? = null

    @Deprecated("Use create instead", replaceWith = ReplaceWith("create(context, judo)"))
    override fun createApiService(
        context: Context,
        judo: Judo,
    ): RecommendationApiService = create(context, judo)

    override fun create(
        context: Context,
        judo: Judo,
    ): RecommendationApiService {
        return createRetrofit(
            context.applicationContext,
            judo,
            // This base URL is never used later on, but is required by Retrofit to be provided.
            LOCALHOST_URL,
        ).create(RecommendationApiService::class.java)
    }

    override fun addInterceptors(
        client: OkHttpClient.Builder,
        context: Context,
        judo: Judo,
    ) {
        super.addInterceptors(client, context, judo)
        client.interceptors().add(RecommendationHeadersInterceptor(judo.authorization))
    }

    override fun getOkHttpClient(
        context: Context,
        judo: Judo,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        setRecommendationCallTimeout(builder, judo)
        addInterceptors(builder, context, judo)

        return builder.build()
    }

    private fun setRecommendationCallTimeout(
        builder: OkHttpClient.Builder,
        judo: Judo,
    ) {
        val timeout = judo.recommendationConfiguration?.timeout ?: RECOMMENDATION_API_DEFAULT_TIMEOUT_SECONDS
        builder.callTimeout(timeout.toLong(), TimeUnit.SECONDS)
    }
}
