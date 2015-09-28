package com.judopay.payment;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface PaymentsApiService {

    @POST("transactions/payments")
    Call<PaymentResponse> payment(@Body Transaction transaction);

}
