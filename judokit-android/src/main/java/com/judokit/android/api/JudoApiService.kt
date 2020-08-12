package com.judokit.android.api

import com.judokit.android.api.model.request.BankSaleRequest
import com.judokit.android.api.model.request.CheckCardRequest
import com.judokit.android.api.model.request.GooglePayRequest
import com.judokit.android.api.model.request.IdealSaleRequest
import com.judokit.android.api.model.request.PaymentRequest
import com.judokit.android.api.model.request.RegisterCardRequest
import com.judokit.android.api.model.request.SaveCardRequest
import com.judokit.android.api.model.request.TokenRequest
import com.judokit.android.api.model.response.BankSaleResponse
import com.judokit.android.api.model.response.BankSaleStatusResponse
import com.judokit.android.api.model.response.CardVerificationResult
import com.judokit.android.api.model.response.IdealSaleResponse
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.Receipt
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Judo interface with Retrofit annotated list of judo API calls that can be performed.
 * Use the [com.judokit.android.Judo.getApiService]  method to obtain an
 * instance. See [GitHub](https://github.com/square/retrofit) for details.
 */
interface JudoApiService {
    /**
     * Perform a payment transaction
     *
     * @param paymentRequest the details for the payment, including the payment method, amount and Judo ID
     * @return the receipt for the payment with the status of the transaction
     */
    @POST("transactions/payments")
    fun payment(@Body paymentRequest: PaymentRequest): Call<JudoApiCallResult<Receipt>>

    /**
     * Perform a pre-auth transaction
     *
     * @param paymentRequest the details for the pre-auth, including the payment method, amount and Judo ID
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/preauths")
    fun preAuthPayment(@Body paymentRequest: PaymentRequest): Call<JudoApiCallResult<Receipt>>

    /**
     * Perform a token payment using a tokenised card
     *
     * @param tokenRequest the payment details for making the transaction
     * @return the receipt for the token payment with the status of the transaction
     */
    @POST("transactions/payments")
    fun tokenPayment(@Body tokenRequest: TokenRequest): Call<JudoApiCallResult<Receipt>>

    /**
     * Perform a token pre-auth using a tokenised card
     *
     * @param tokenRequest the token card details
     * @return the receipt for the pre-auth with the status of the transaction
     */
    @POST("transactions/preauths")
    fun preAuthTokenPayment(@Body tokenRequest: TokenRequest): Call<JudoApiCallResult<Receipt>>

    /**
     * Complete a transaction that required 3D-Secure verification by providing the 3D-Secure response data.
     *
     * @param receiptId the receipt ID from the original transaction
     * @param cardVerificationResult the 3D-Secure details returned from successfully validating the card with the merchant bank
     * @return the receipt for the transaction
     */
    @PUT("transactions/{receiptId}")
    fun complete3dSecure(
        @Path("receiptId") receiptId: String,
        @Body cardVerificationResult: CardVerificationResult
    ): Call<JudoApiCallResult<Receipt>>

    /**
     * Register a card to be used for making future tokenised payments
     *
     * @param registerCardRequest the details of the card to be registered
     * @return the receipt for the card registration with the status of the transaction
     */
    @POST("transactions/registercard")
    fun registerCard(@Body registerCardRequest: RegisterCardRequest): Call<JudoApiCallResult<Receipt>>

    /**
     * Save a card to be used for making future tokenised payments
     *
     * @param saveCardRequest the details of the card to be saved
     * @return the receipt for the card save with the status of the transaction
     */
    @POST("transactions/savecard")
    fun saveCard(@Body saveCardRequest: SaveCardRequest): Call<JudoApiCallResult<Receipt>>

    /**
     * Performs a card check against the card. This doesn't do an authorisation, it just checks whether or not the card is valid
     *
     * @param checkCardRequest the details of the card to be checked
     * @return the receipt for the card check with the status of the transaction
     */
    @POST("transactions/checkcard")
    fun checkCard(@Body checkCardRequest: CheckCardRequest): Call<JudoApiCallResult<Receipt>>

    @POST("transactions/payments")
    fun googlePayPayment(@Body googlePayRequest: GooglePayRequest): Call<JudoApiCallResult<Receipt>>

    @POST("transactions/preauths")
    fun preAuthGooglePayPayment(@Body googlePayRequest: GooglePayRequest): Call<JudoApiCallResult<Receipt>>

    @POST("order/bank/sale")
    fun sale(@Body saleRequest: IdealSaleRequest): Call<JudoApiCallResult<IdealSaleResponse>>

    @GET("order/bank/statusrequest/{orderID}")
    fun status(@Path("orderID") orderId: String): Call<JudoApiCallResult<BankSaleStatusResponse>>

    @POST("order/bank/sale")
    fun sale(@Body saleRequest: BankSaleRequest): Call<JudoApiCallResult<BankSaleResponse>>
}
