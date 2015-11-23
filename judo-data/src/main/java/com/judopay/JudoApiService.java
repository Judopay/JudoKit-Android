package com.judopay;

import com.judopay.payment.Collection;
import com.judopay.payment.PaymentTransaction;
import com.judopay.payment.Receipt;
import com.judopay.payment.Receipts;
import com.judopay.payment.Refund;
import com.judopay.payment.ThreeDSecureInfo;
import com.judopay.payment.TokenTransaction;
import com.judopay.register.RegisterTransaction;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface JudoApiService {

    /**
     * Perform a payment transaction
     *
     * @param paymentTransaction
     * @return
     */
    @POST("transactions/payments")
    Observable<Receipt> payment(@Body PaymentTransaction paymentTransaction);

    /**
     * Perform a token payment using a tokenised card
     *
     * @param transaction the payment details for making the transaction
     * @return an Observable Receipt object containing the receipt for the transaction
     */
    @POST("transactions/payments")
    Observable<Receipt> tokenPayment(@Body TokenTransaction transaction);

    /**
     * Perform a payment using the returned data from a 3D-Secure authorisatino
     *
     * @param receiptId        the receipt ID from the original transaction
     * @param threeDSecureInfo the 3D-Secure details returned from successfully validating the card with the merchant bank
     * @return an Observable Receipt object containing the receipt for the transaction
     */
    @PUT("transactions/{receiptId}")
    Observable<Receipt> threeDSecurePayment(@Path("receiptId") String receiptId, @Body ThreeDSecureInfo threeDSecureInfo);

    /**
     * @param collection
     * @return
     */
    @POST("transactions/collections")
    Observable<Receipt> collection(@Body Collection collection);

    /**
     * @param refund
     * @return
     */
    @POST("transactions/refunds")
    Observable<Receipt> refund(@Body Refund refund);

    /**
     * @param paymentTransaction
     * @return
     */
    @POST("transactions/preauths")
    Observable<Receipt> preAuth(@Body PaymentTransaction paymentTransaction);

    /**
     * @param transaction
     * @return
     */
    @POST("transactions/registercard")
    Observable<Receipt> registerCard(@Body RegisterTransaction transaction);

    /**
     * List all payment receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return an Observable Receipts object containing the list of payment receipts
     */
    @GET("transactions/preauths")
    Observable<Receipts> paymentReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all pre-auth receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return an Observable Receipts object containing the list of pre-auth receipts
     */
    @GET("transactions/preauths")
    Observable<Receipts> preAuthReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all refund receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return an Observable Receipts object containing the list of refund receipts
     */
    @GET("transactions/refunds")
    Observable<Receipts> refundReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all collection receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return an Observable Receipts object containing the list of collection receipts
     */
    @GET("transactions/collections")
    Observable<Receipts> collectionReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all consumer receipts for a consumer token
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return an Observable Receipts object containing the list of consumer receipts for the consumerToken
     */
    @GET("consumers/{consumerToken}")
    Observable<Receipts> consumerReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     *
     * List receipts for a consumer matching a given receiptId
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param receiptId     the receipt ID to find consumer receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return an Observable Receipts object containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/{receiptId}")
    Observable<Receipts> consumerReceipts(@Path("consumerToken") String consumerToken, @Path("receiptId") String receiptId, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

}