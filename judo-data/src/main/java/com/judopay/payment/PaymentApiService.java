package com.judopay.payment;

import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

public interface PaymentApiService {

    @POST("transactions/payments")
    Observable<Receipt> payment(@Body Transaction transaction);

    @POST("transactions/payments")
    Observable<Receipt> tokenPayment(@Body TokenTransaction transaction);

    @PUT("transactions/{receiptId}")
    Observable<Receipt> threeDSecurePayment(@Path("receiptId") String receiptId, @Body ThreeDSecureInfo threeDSecureInfo);

    @POST("transactions/collections")
    Observable<Receipt> collection(@Body Collection collection);

    @POST("transactions/refunds")
    Observable<Receipt> refund(@Body Refund refund);

    @POST("transactions/preauths")
    Observable<Receipt> preAuth(@Body Transaction transaction);

}