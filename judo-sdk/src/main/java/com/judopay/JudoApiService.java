package com.judopay;

import android.content.Context;

import com.judopay.model.CardVerificationResult;
import com.judopay.model.CheckCardRequest;
import com.judopay.model.CollectionRequest;
import com.judopay.model.GooglePayRequest;
import com.judopay.model.PaymentRequest;
import com.judopay.model.Receipt;
import com.judopay.model.Receipts;
import com.judopay.model.RefundRequest;
import com.judopay.model.RegisterCardRequest;
import com.judopay.model.SaleRequest;
import com.judopay.model.SaleResponse;
import com.judopay.model.SaleStatusRequest;
import com.judopay.model.SaleStatusResponse;
import com.judopay.model.SaveCardRequest;
import com.judopay.model.TokenRequest;
import com.judopay.model.VCOPaymentRequest;
import com.judopay.model.VoidRequest;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Judo interface with Retrofit annotated list of judo API calls that can be performed.
 * Use the {@link com.judopay.Judo#getApiService(Context)}  method to obtain an
 * instance. See <a href="https://github.com/square/retrofit">GitHub</a> for details.
 */
@SuppressWarnings("SameParameterValue")
public interface JudoApiService {

    /**
     * Perform a payment transaction
     *
     * @param paymentRequest the details for the payment, including the payment method, amount and Judo ID
     * @return the receipt for the payment with the status of the transaction
     */
    @POST("transactions/payments")
    Single<Receipt> payment(@Body PaymentRequest paymentRequest);

    /**
     * Perform a pre-auth transaction
     *
     * @param paymentRequest the details for the pre-auth, including the payment method, amount and Judo ID
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/preauths")
    Single<Receipt> preAuth(@Body PaymentRequest paymentRequest);

    /**
     * Perform a token payment using a tokenised card
     *
     * @param tokenRequest the payment details for making the transaction
     * @return the receipt for the token payment with the status of the transaction
     */
    @POST("transactions/payments")
    Single<Receipt> tokenPayment(@Body TokenRequest tokenRequest);

    /**
     * Perform a token pre-auth using a tokenised card
     *
     * @param tokenRequest the token card details
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/preauths")
    Single<Receipt> tokenPreAuth(@Body TokenRequest tokenRequest);

    /**
     * Void a pre-auth transaction, releasing funds back to the card holder.
     *
     * @param voidRequest the details from the pre-auth for voiding the transaction
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/voids")
    Single<Receipt> voidPreAuth(@Body VoidRequest voidRequest);

    /**
     * Complete a transaction that required 3D-Secure verification by providing the 3D-Secure response data.
     *
     * @param receiptId              the receipt ID from the original transaction
     * @param cardVerificationResult the 3D-Secure details returned from successfully validating the card with the merchant bank
     * @return the receipt for the transaction
     */
    @PUT("transactions/{receiptId}")
    Single<Receipt> complete3dSecure(@Path("receiptId") String receiptId, @Body CardVerificationResult cardVerificationResult);

    /**
     * @param collectionRequest the collectionRequest transaction to be performed
     * @return the receipt for the collectionRequest with the status of the transaction
     */
    @POST("transactions/collections")
    Single<Receipt> collection(@Body CollectionRequest collectionRequest);

    /**
     * Perform a refundRequest for a transaction
     *
     * @param refundRequest the object containing the amount to be refunded and receiptId
     * @return the receipt for the refundRequest with the status of the transaction
     */
    @POST("transactions/refunds")
    Single<Receipt> refund(@Body RefundRequest refundRequest);

    /**
     * Register a card to be used for making future tokenised payments
     *
     * @param registerCardRequest the details of the card to be registered
     * @return the receipt for the card registration with the status of the transaction
     */
    @POST("transactions/registercard")
    Single<Receipt> registerCard(@Body RegisterCardRequest registerCardRequest);

    /**
     * Save a card to be used for making future tokenised payments
     *
     * @param saveCardRequest the details of the card to be saved
     * @return the receipt for the card save with the status of the transaction
     */
    @POST("transactions/savecard")
    Single<Receipt> saveCard(@Body SaveCardRequest saveCardRequest);

    /**
     * Performs a card check against the card. This doesn't do an authorisation, it just checks whether or not the card is valid
     *
     * @param checkCardRequest the details of the card to be checked
     * @return the receipt for the card check with the status of the transaction
     */
    @POST("transactions/checkcard")
    Single<Receipt> checkCard(@Body CheckCardRequest checkCardRequest);

    @POST("transactions/payments")
    Single<Receipt> googlePayPayment(@Body GooglePayRequest googlePayRequest);

    @POST("transactions/preauths")
    Single<Receipt> googlePayPreAuth(@Body GooglePayRequest googlePayRequest);

    @POST("transactions/payments")
    Single<Receipt> vcoPayment(@Body VCOPaymentRequest vcoPaymentRequest);

    @POST("transactions/preauths")
    Single<Receipt> vcoPreAuth(@Body VCOPaymentRequest vcoPaymentRequest);

    @POST("order/bank/sale")
    Single<SaleResponse> sale(@Body SaleRequest saleRequest);

    @POST("order/statusrequest")
    Observable<SaleStatusResponse> status(@Body SaleStatusRequest saleStatusRequest);

    /**
     * List all payment receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of payment receipts
     */
    @GET("transactions/payments")
    Single<Receipts> paymentReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all pre-auth receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of pre-auth receipts
     */
    @GET("transactions/preauths")
    Single<Receipts> preAuthReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all refund receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of refund receipts
     */
    @GET("transactions/refunds")
    Single<Receipts> refundReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * List all collection receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return Receipts containing the list of collection receipts
     */
    @GET("transactions/collections")
    Single<Receipts> collectionReceipts(@Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    /**
     * @param receiptId the receipt ID to use for finding the receipt
     * @param pageSize  maximum number of results to return, default is 10
     * @param offset    the position to return results from, default is 0
     * @param sort      the sort type to be used, can be either {@code time-ascending} or {@code time-descending}
     * @return the receipt matched with the receiptId
     */
    @GET("transactions/{receiptId}")
    Single<Receipt> findReceipt(@Path("receiptId") String receiptId, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

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
    Single<Receipts> consumerReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

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
    Single<Receipts> consumerPaymentReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

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
    Single<Receipts> consumerPreAuthReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

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
    Single<Receipts> consumerCollectionReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

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
    Single<Receipts> consumerRefundReceipts(@Path("consumerToken") String consumerToken, @Query("pageSize") Integer pageSize, @Query("offset") Integer offset, @Query("sort") String sort);

    @POST("/msbox/id/{clientAccessKey}")
    Single<Object> phoneVerification(@Path("clientAccessKey") String clientAccessKey, @Body String evurl);
}
