package com.judopay.judokit.android.api.model

import com.judopay.judokit.android.requireNotNullOrEmpty
import kotlinx.parcelize.Parcelize
import okhttp3.Headers

private const val API_TOKEN_HEADER = "Api-Token"
private const val PAYMENT_SESSION_HEADER = "Payment-Session"

/**
 * Authorization type that uses token and one time use payment session combination to authorize
 * Judo backend requests.
 * @param paymentSession One time use token that is generated via an API call.
 * @param apiToken Token provided by JudoPay.
 */
@Parcelize
class PaymentSessionAuthorization internal constructor(
    private val paymentSession: String,
    private val apiToken: String,
) : Authorization {
    class Builder {
        private var paymentSession: String? = null
        private var apiToken: String? = null

        fun setPaymentSession(paymentSession: String?) = apply { this.paymentSession = paymentSession }

        fun setApiToken(apiToken: String?) = apply { this.apiToken = apiToken }

        fun build(): PaymentSessionAuthorization {
            val myPaymentSession = requireNotNullOrEmpty(paymentSession, "paymentSession")
            val myToken = requireNotNullOrEmpty(apiToken, "apiToken")

            return PaymentSessionAuthorization(myPaymentSession, myToken)
        }
    }

    override val headers: Headers
        get() =
            Headers.Builder()
                .add(API_TOKEN_HEADER, apiToken)
                .add(PAYMENT_SESSION_HEADER, paymentSession).build()
}
