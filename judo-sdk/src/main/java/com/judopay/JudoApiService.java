package com.judopay;

import android.content.Context;

import com.judopay.model.AndroidPayRequest;
import com.judopay.model.CollectionRequest;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.Receipts;
import com.judopay.model.RefundRequest;
import com.judopay.model.RegisterCardRequest;
import com.judopay.model.ThreeDSecureInfo;
import com.judopay.model.TokenRequest;
import com.judopay.model.VoidRequest;

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
     * @param paymentRequest the details for the payment, including the payment method, amount and Judo ID
     * @return the receipt for the payment with the status of the transaction
     */
    @POST("transactions/payments")
    Observable<Receipt> payment(@Body PaymentRequest paymentRequest);

    /**
     * Perform a pre-auth transaction
     *
     * @param paymentRequest the details for the pre-auth, including the payment method, amount and Judo ID
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/preauths")
    Observable<Receipt> preAuth(@Body PaymentRequest paymentRequest);

    /**
     * Perform a token payment using a tokenised card
     *
     * @param tokenRequest the payment details for making the transaction
     * @return the receipt for the token payment with the status of the transaction
     */
    @POST("transactions/payments")
    Observable<Receipt> tokenPayment(@Body TokenRequest tokenRequest);

    /**
     * Perform a token pre-auth using a tokenised card
     *
     * @param tokenRequest the token card details
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/preauths")
    Observable<Receipt> tokenPreAuth(@Body TokenRequest tokenRequest);

    /**
     * Void a pre-auth transaction, releasing funds back to the card holder.
     *
     * @param voidRequest the details from the pre-auth for voiding the transaction
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/voids")
    Observable<Receipt> voidPreAuth(@Body VoidRequest voidRequest);

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
     * @param collectionRequest the collectionRequest transaction to be performed
     * @return the receipt for the collectionRequest with the status of the transaction
     */
    @POST("transactions/collections")
    Observable<Receipt> collection(@Body CollectionRequest collectionRequest);

    /**
     * Perform a refundRequest for a transaction
     *
     * @param refundRequest the object containing the amount to be refunded and receiptId
     * @return the receipt for the refundRequest with the status of the transaction
     */
    @POST("transactions/refunds")
    Observable<Receipt> refund(@Body RefundRequest refundRequest);

    /**
     * Register a card to be used for making future tokenised payments
     *
     * @param registerCardRequest the details of the card to be registered
     * @return the receipt for the card registration with the status of the transaction
     */
    @POST("transactions/registercard")
    Observable<Receipt> registerCard(@Body RegisterCardRequest registerCardRequest);

    @POST("transactions/androidpay/payment")
    Observable<Receipt> androidPayPayment(@Body AndroidPayRequest androidPayRequest);

    @POST("transactions/androidpay/preauth")
    Observable<Receipt> androidPayPreAuth(@Body AndroidPayRequest androidPayRequest);

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