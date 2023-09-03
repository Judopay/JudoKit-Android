package com.judopay.judokit.android.api.factory

import android.content.Context
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.apiBaseUrl

/**
 * Factory that provides the [JudoApiService] used for performing all HTTP requests to the
 * judoPay APIs. As implementation of the ApiService requires some configuration, it is better
 * to use a shared instance than create a new instance per request, so this class ensures that only
 * one instance is used in the application.
 */
object JudoApiServiceFactory: ApiServiceFactory() {

    /**
     * @param context the calling Context
     * @param judo the judo instance
     * @return the Retrofit API service implementation containing the methods used
     * for interacting with the judoPay REST API.
     */
    override fun create(context: Context, judo: Judo): JudoApiService =
        createRetrofit(context.applicationContext, judo, judo.apiBaseUrl)
            .create(JudoApiService::class.java)
}
