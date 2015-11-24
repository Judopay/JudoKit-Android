package com.judopay.transaction;

import com.judopay.payment.Receipt;

import retrofit.http.GET;
import rx.Observable;

public interface TransactionApiService {

    @GET("transactions/")
    Observable<Receipt> receipts();

    @GET("consumers/{consumerToken}")
    Observable<Receipt> consumerReceipts(String consumerToken);

}