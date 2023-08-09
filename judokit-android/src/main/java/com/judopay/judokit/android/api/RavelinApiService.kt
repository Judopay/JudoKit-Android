package com.judopay.judokit.android.api

import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.RavelinEncryptionResponse
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

// Todo: Adjust comment.
/**
 * Judo interface with Retrofit annotated list of judo API calls that can be performed.
 * Use the [createApiService][com.judopay.judokit.android.api.factory.JudoApiServiceFactory.createJudoApiService]
 * method to obtain an instance. See [GitHub](https://github.com/square/retrofit) for details.
 */
interface RavelinApiService {

    // Todo: Adjust comment.
    /**
     * A method used to fetch the details of a transaction based on a provided receipt ID
     *
     * @param receiptId - a string which contains the receipt ID of a transaction.
     * @return the receipt object that corresponds to the passed receipt ID
     */
//    @POST("{ravelinEncryptionEndpointUrl}")
//    fun encryptCard(
//        @Path("ravelinEncryptionEndpointUrl") ravelinEncryptionEndpointUrl: String,
//        @Body encryptCardRequest: EncryptCardRequest
//    ): Call<JudoApiCallResult<RavelinEncryptionResponse>>
}
