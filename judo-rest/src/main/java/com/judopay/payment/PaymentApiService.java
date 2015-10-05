package com.judopay.payment;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface PaymentApiService {

    @POST("transactions/payments")
    Observable<PaymentResponse> payment(@Body Transaction transaction);

}