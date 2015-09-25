package com.judopay.payment;

import retrofit.http.POST;

public interface PaymentsApiService {

    @POST("transactions/payments")
    PaymentResponse payment(Transaction transaction);

}
