package com.judopay.judokit.android.api.factory

import android.content.Context
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.RecommendationApiService

/**
 * Factory that provides the [RecommendationApiService] used for performing all HTTP requests to the
 * Recommendation APIs. As implementation of the ApiService requires some configuration, it is better
 * to use a shared instance than create a new instance per request, so this class ensures that only
 * one instance is used in the application.
 */
object RecommendationApiServiceFactory: ApiServiceFactory() {

    /**
     * @param context the calling Context
     * @param judo the judo instance
     * @return the Retrofit API service implementation containing the methods used
     * for interacting with the Recommendation REST API.
     */
    @JvmStatic
    fun createRecommendationApiService(context: Context, judo: Judo): RecommendationApiService {
        return createRetrofit(
            context.applicationContext,
            judo,
            // This base URL is never used later on, but is required by Retrofit to be provided.
            "http://localhost/"
        ).create(RecommendationApiService::class.java)
    }
}
