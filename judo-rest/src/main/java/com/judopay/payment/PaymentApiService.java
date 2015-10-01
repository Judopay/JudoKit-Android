package com.judopay.payment;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface PaymentApiService {

    @POST("transactions/payments")
    Call<PaymentResponse> payment(@Body Transaction transaction);

}
