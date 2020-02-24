package com.judopay.api

import com.judopay.api.model.request.*
import com.judopay.api.model.response.CardVerificationResult
import com.judopay.api.model.response.Receipt
import com.judopay.api.model.response.Receipts
import io.reactivex.Single
import retrofit2.http.*

/**
 * Judo interface with Retrofit annotated list of judo API calls that can be performed.
 * Use the [com.judopay.Judo.getApiService]  method to obtain an
 * instance. See [GitHub](https://github.com/square/retrofit) for details.
 */
interface JudoApiService {
    /**
     * Perform a payment transaction
     *
     * @param paymentRequest the details for the payment, including the payment method, amount and Judo ID
     * @return the receipt for the payment with the status of the transaction
     */
//    @POST("transactions/payments")
//    fun payment(@Body paymentRequest: PaymentRequest): Single<Receipt>

    /**
     * Perform a pre-auth transaction
     *
     * @param paymentRequest the details for the pre-auth, including the payment method, amount and Judo ID
     * @return the receipt for the pre-auth with the status of the transaction
     */
//    @POST("transactions/preauths")
//    fun preAuth(@Body paymentRequest: PaymentRequest): Single<Receipt>

    /**
     * Perform a token payment using a tokenised card
     *
     * @param tokenRequest the payment details for making the transaction
     * @return the receipt for the token payment with the status of the transaction
     */
//    @POST("transactions/payments")
//    fun tokenPayment(@Body tokenRequest: TokenRequest): Single<Receipt>

    /**
     * Perform a token pre-auth using a tokenised card
     *
     * @param tokenRequest the token card details
     * @return the receipt for the pre-auth with the status of the transaction
     */
//    @POST("transactions/preauths")
//    fun tokenPreAuth(@Body tokenRequest: TokenRequest): Single<Receipt>

    /**
     * Void a pre-auth transaction, releasing funds back to the card holder.
     *
     * @param voidRequest the details from the pre-auth for voiding the transaction
     * @return the receipt for the pre-auth with the status of the transaction
     */
//    @POST("transactions/voids")
//    fun voidPreAuth(@Body voidRequest: VoidRequest): Single<Receipt>

    /**
     * Complete a transaction that required 3D-Secure verification by providing the 3D-Secure response data.
     *
     * @param receiptId              the receipt ID from the original transaction
     * @param cardVerificationResult the 3D-Secure details returned from successfully validating the card with the merchant bank
     * @return the receipt for the transaction
     */
    @PUT("transactions/{receiptId}")
    fun complete3dSecure(@Path("receiptId") receiptId: String,
                         @Body cardVerificationResult: CardVerificationResult): Single<Receipt>

    /**
     * @param collectionRequest the collectionRequest transaction to be performed
     * @return the receipt for the collectionRequest with the status of the transaction
     */
//    @POST("transactions/collections")
//    fun collection(@Body collectionRequest: CollectionRequest): Single<Receipt>

    /**
     * Perform a refundRequest for a transaction
     *
     * @param refundRequest the object containing the amount to be refunded and receiptId
     * @return the receipt for the refundRequest with the status of the transaction
     */
//    @POST("transactions/refunds")
//    fun refund(@Body refundRequest: RefundRequest): Single<Receipt>

    /**
     * Register a card to be used for making future tokenised payments
     *
     * @param registerCardRequest the details of the card to be registered
     * @return the receipt for the card registration with the status of the transaction
     */
//    @POST("transactions/registercard")
//    fun registerCard(@Body registerCardRequest: RegisterCardRequest): Single<Receipt>

    /**
     * Save a card to be used for making future tokenised payments
     *
     * @param saveCardRequest the details of the card to be saved
     * @return the receipt for the card save with the status of the transaction
     */
//    @POST("transactions/savecard")
//    fun saveCard(@Body saveCardRequest: SaveCardRequest): Single<Receipt>

    /**
     * Performs a card check against the card. This doesn't do an authorisation, it just checks whether or not the card is valid
     *
     * @param checkCardRequest the details of the card to be checked
     * @return the receipt for the card check with the status of the transaction
     */
//    @POST("transactions/checkcard")
//    fun checkCard(@Body checkCardRequest: CheckCardRequest): Single<Receipt>

//    @POST("transactions/payments")
//    fun googlePayPayment(@Body googlePayRequest: GooglePayRequest): Single<Receipt>
//
//    @POST("transactions/preauths")
//    fun googlePayPreAuth(@Body googlePayRequest: GooglePayRequest): Single<Receipt>
//
//    @POST("transactions/payments")
//    fun vcoPayment(@Body vcoPaymentRequest: VCOPaymentRequest): Single<Receipt>
//
//    @POST("transactions/preauths")
//    fun vcoPreAuth(@Body vcoPaymentRequest: VCOPaymentRequest): Single<Receipt>
//
//    @POST("order/bank/sale")
//    fun sale(@Body saleRequest: SaleRequest): Single<SaleResponse>
//
//    @GET("order/bank/statusrequest/{orderID}")
//    fun status(@Path("orderID") orderId: String): Single<SaleStatusResponse>

    /**
     * List all payment receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return Receipts containing the list of payment receipts
     */
    @GET("transactions/payments")
    fun paymentReceipts(@Query("pageSize") pageSize: Int,
                        @Query("offset") offset: Int,
                        @Query("sort") sort: String): Single<Receipts>

    /**
     * List all pre-auth receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return Receipts containing the list of pre-auth receipts
     */
    @GET("transactions/preauths")
    fun preAuthReceipts(@Query("pageSize") pageSize: Int,
                        @Query("offset") offset: Int,
                        @Query("sort") sort: String): Single<Receipts>

    /**
     * List all refund receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return Receipts containing the list of refund receipts
     */
    @GET("transactions/refunds")
    fun refundReceipts(@Query("pageSize") pageSize: Int,
                       @Query("offset") offset: Int,
                       @Query("sort") sort: String): Single<Receipts>

    /**
     * List all collection receipts for the account
     *
     * @param pageSize maximum number of results to return, default is 10
     * @param offset   the position to return results from, default is 0
     * @param sort     the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return Receipts containing the list of collection receipts
     */
    @GET("transactions/collections")
    fun collectionReceipts(@Query("pageSize") pageSize: Int,
                           @Query("offset") offset: Int,
                           @Query("sort") sort: String): Single<Receipts>

    /**
     * @param receiptId the receipt ID to use for finding the receipt
     * @param pageSize  maximum number of results to return, default is 10
     * @param offset    the position to return results from, default is 0
     * @param sort      the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return the receipt matched with the receiptId
     */
    @GET("transactions/{receiptId}")
    fun findReceipt(@Path("receiptId") receiptId: String,
                    @Query("pageSize") pageSize: Int,
                    @Query("offset") offset: Int,
                    @Query("sort") sort: String): Single<Receipt>

    /**
     * List all consumer receipts for a consumer token
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return Receipts containing the list of consumer receipts for the consumerToken
     */
    @GET("consumers/{consumerToken}")
    fun consumerReceipts(@Path("consumerToken") consumerToken: String,
                         @Query("pageSize") pageSize: Int,
                         @Query("offset") offset: Int,
                         @Query("sort") sort: String): Single<Receipts>

    /**
     * List all payment receipts for a consumer
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/payments")
    fun consumerPaymentReceipts(@Path("consumerToken") consumerToken: String,
                                @Query("pageSize") pageSize: Int,
                                @Query("offset") offset: Int,
                                @Query("sort") sort: String): Single<Receipts>

    /**
     * List all pre-auth receipts for a consumer
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/preauths")
    fun consumerPreAuthReceipts(@Path("consumerToken") consumerToken: String,
                                @Query("pageSize") pageSize: Int,
                                @Query("offset") offset: Int,
                                @Query("sort") sort: String): Single<Receipts>

    /**
     * List all collection receipts for a consumer
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/collections")
    fun consumerCollectionReceipts(@Path("consumerToken") consumerToken: String,
                                   @Query("pageSize") pageSize: Int,
                                   @Query("offset") offset: Int,
                                   @Query("sort") sort: String): Single<Receipts>

    /**
     * List all refund receipts for a consumer
     *
     * @param consumerToken the consumer to use for finding receipts for
     * @param pageSize      maximum number of results to return, default is 10
     * @param offset        the position to return results from, default is 0
     * @param sort          the sort type to be used, can be either `time-ascending` or `time-descending`
     * @return Receipts containing the list of consumer receipts for the consumerToken and receiptId
     */
    @GET("consumers/{consumerToken}/refunds")
    fun consumerRefundReceipts(@Path("consumerToken") consumerToken: String,
                               @Query("pageSize") pageSize: Int,
                               @Query("offset") offset: Int,
                               @Query("sort") sort: String): Single<Receipts>
}