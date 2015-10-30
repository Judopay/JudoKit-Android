package com.judopay.payment;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.PUT;
import rx.Observable;

public interface PaymentApiService {

    @POST("transactions/payments")
    Observable<PaymentResponse> payment(@Body Transaction transaction);

    @PUT("transactions/{receiptId}")
    Observable<PaymentResponse> threeDSecurePayment(String receiptId, @Body ThreeDSecureInfo threeDSecureInfo);

    @POST("transactions/collections")
    Observable<PaymentResponse> collection(@Body Collection collection);

    @POST("transactions/refunds")
    Observable<PaymentResponse> refund(@Body Refund refund);

    @POST("transactions/preauths")
    Observable<PaymentResponse> preAuth(@Body Transaction transaction);

}