package com.judokit.android.examples.apiclient

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class CreatePaymentSessionRequest(
    val judoId: String,
    val amount: String,
    val currency: String,
    val yourConsumerReference: String,
    val yourPaymentReference: String,
)

data class CreatePaymentSessionResponse(
    val payByLinkUrl: String,
    val postUrl: String,
    val reference: String,
)

public interface ApiClient {
    @POST("/webpayments/payments")
    fun createPaymentSession(
        @Body request: CreatePaymentSessionRequest,
    ): Call<CreatePaymentSessionResponse>
}
