package com.judopay.transaction;

import com.judopay.payment.PaymentResponse;

import retrofit.http.GET;
import rx.Observable;

public interface TransactionApiService {

    @GET("transactions/")
    Observable<PaymentResponse> receipts();

    @GET("consumers/{consumerToken}")
    Observable<PaymentResponse> consumerReceipts(String consumerToken);

}