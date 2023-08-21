package com.judopay.judokit.android.api

import com.judopay.judokit.android.api.model.request.RecommendationRequest
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.RecommendationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Recommendation interface with Retrofit annotated list of recommendation API calls that can be performed.
 * Use the [createApiService][com.judopay.judokit.android.api.factory.JudoApiServiceFactory.createRecommendationApiService]
 * method to obtain an instance. See [GitHub](https://github.com/square/retrofit) for details.
 */
interface RecommendationApiService {

    /**
     * A method used to fetch the recommendation status, based on provided encrypted card details.
     */
    @POST()
    fun requestRecommendation(
        @Url url: String,
        @Body recommendationRequest: RecommendationRequest
    ): Call<JudoApiCallResult<RecommendationResponse>>
}
