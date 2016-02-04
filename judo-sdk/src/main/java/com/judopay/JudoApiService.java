package com.judopay;

import android.content.Context;

import com.judopay.model.Collection;
import com.judopay.model.PaymentTransaction;
import com.judopay.model.Receipt;
import com.judopay.model.Receipts;
import com.judopay.model.Refund;
import com.judopay.model.RegisterTransaction;
import com.judopay.model.ThreeDSecureInfo;
import com.judopay.model.TokenTransaction;
import com.judopay.model.VoidTransaction;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Judo interface with Retrofit annotated list of judo API calls that can be performed.
 * Use the {@link com.judopay.api.JudoApiServiceFactory#getInstance(Context)} method to obtain an
 * instance. See <a href="https://github.com/square/retrofit">GitHub</a> for details.
 */
public interface JudoApiService {

    /**
     * Perform a payment transaction
     *
     * @param transaction the details for the payment, including the payment method, amount and Judo ID
     * @return the receipt for the payment with the status of the transaction
     */
    @POST("transactions/payments")
    Observable<Receipt> payment(@Body PaymentTransaction transaction);

    /**
     * Perform a pre-auth transaction
     *
     * @param transaction the details for the pre-auth, including the payment method, amount and Judo ID
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/preauths")
    Observable<Receipt> preAuth(@Body PaymentTransaction transaction);

    /**
     * Perform a token payment using a tokenised card
     *
     * @param transaction the payment details for making the transaction
     * @return the receipt for the token payment with the status of the transaction
     */
    @POST("transactions/payments")
    Observable<Receipt> tokenPayment(@Body TokenTransaction transaction);

    /**
     * Perform a token pre-auth using a tokenised card
     *
     * @param transaction the token card details
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/preauths")
    Observable<Receipt> tokenPreAuth(@Body TokenTransaction transaction);

    /**
     * Void a pre-auth transaction, releasing funds back to the card holder.
     *
     * @param transaction the details from the pre-auth for voiding the transaction
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/voids")
    Observable<Receipt> voidPreAuth(@Body VoidTransaction transaction);

    /**
     * Complete a transaction that required 3D-Secure verification by providing the 3D-Secure response data.
     *
     * @param receiptId        the receipt ID from the original transaction
     * @param threeDSecureInfo the 3D-Secure details returned from successfully validating the card with the merchant bank
     * @return the receipt for the transaction
     */
    @PUT("transactions/{receiptId}")
    Observable<Receipt> complete3dSecure(@Path("receiptId") String receiptId, @Body ThreeDSecureInfo threeDSecureInfo);

    /**
     * @param collection the collection transaction to be performed
     * @return the receipt for the collection with the status of the transaction
     */
    @POST("transactions/collections")
    Observable<Receipt> collection(@Body Collection collection);

    /**
     * Perform a refund for a transaction
     *
     * @param refund the object containing the amount to be refunded and receiptId
     * @return the receipt for the refund with the status of the transaction
     */
    @POST("transactions/refunds")
    Observable<Receipt> refund(@Body Refund refund);

    /**
     * Register a card to be used for making future tokenised payments
     *
     * @param transaction the details for the payment, including the payment method, amount and Judo account ID
     * @return the receipt for the card registration with the status of the transaction
     */
    @POST("transactions/registercard")
    Observable<Receipt> registerCard(@Body RegisterTransaction transaction);

    /**
     * List all payment receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of payment receipts
     */
    @GET("transactions/payments")
    Observable<Receipts> paymentReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all pre-auth receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of pre-auth receipts
     */
    @GET("transactions/preauths")
    Observable<Receipts> preAuthReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all refund receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of refund receipts
     */
    @GET("transactions/refunds")
    Observable<Receipts> refundReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all collection receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of collection receipts
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
     * @return Receipts containing the list of consumer receipts for the consumerToken
     */
    @GET("consumers/{consumerToken}")
    Observable<Receipts> consumerReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List receipts for a consumer matching a given receiptId
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param receiptId     the receipt ID to find consumer receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/{receiptId}")
    Observable<Receipts> consumerReceipts(@Path("consumerToken") String consumerToken, @Path("receiptId") String receiptId, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all payment receipts for a consumer
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/payments")
    Observable<Receipts> consumerPaymentReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all payment receipts for a consumer matching a given receiptId
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param receiptId     the receipt ID to find consumer receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/payments/{receiptId}")
    Observable<Receipts> consumerPaymentReceipts(@Path("consumerToken") String consumerToken, @Path("receiptId") String receiptId, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all pre-auth receipts for a consumer
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/preauths")
    Observable<Receipts> consumerPreAuthReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all pre-auth receipts for a consumer matching a given receiptId
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param receiptId     the receipt ID to find consumer receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/preauths/{receiptId}")
    Observable<Receipts> consumerPreAuthReceipts(@Path("consumerToken") String consumerToken, @Path("receiptId") String receiptId, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all collection receipts for a consumer
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/collections")
    Observable<Receipts> consumerCollectionReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all collection receipts for a consumer matching a given receiptId
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param receiptId     the receipt ID to find consumer receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/collections/{receiptId}")
    Observable<Receipts> consumerCollectionReceipts(@Path("consumerToken") String consumerToken, @Path("receiptId") String receiptId, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all refund receipts for a consumer
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/refunds")
    Observable<Receipts> consumerRefundReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all refund receipts for a consumer matching a given receiptId
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param receiptId     the receipt ID to find consumer receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/refunds/{receiptId}")
    Observable<Receipts> consumerRefundReceipts(@Path("consumerToken") String consumerToken, @Path("receiptId") String receiptId, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

}