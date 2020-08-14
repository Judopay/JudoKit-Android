package com.judokit.android.api.model

import android.util.Base64
import com.judokit.android.requireNotNullOrEmpty
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import okhttp3.Headers
import java.nio.charset.StandardCharsets

private const val AUTHORIZATION_HEADER = "Authorization"
private const val PAYMENT_SESSION_HEADER = "Payment-Session"

@Parcelize
class PaymentSessionAuthorization internal constructor(
    private val paymentSession: String,
    private val apiToken: String
) : Authorization {

    class Builder {
        private var paymentSession: String? = null
        private var apiToken: String? = null

        fun setPaymentSession(paymentSession: String?) =
            apply { this.paymentSession = paymentSession }

        fun setApiToken(apiToken: String?) = apply { this.apiToken = apiToken }

        fun build(): PaymentSessionAuthorization {
            val myPaymentSession = requireNotNullOrEmpty(paymentSession, "paymentSession")
            val myToken = requireNotNullOrEmpty(apiToken, "apiToken")

            return PaymentSessionAuthorization(myPaymentSession, myToken)
        }
    }

    @IgnoredOnParcel
    private val encodedCredentials: String =
        Base64.encodeToString("$apiToken:".toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)

    private val authorizationHeaderValue: String
        get() = "Basic $encodedCredentials"

    override val headers: Headers
        get() = Headers.Builder().add(AUTHORIZATION_HEADER, authorizationHeaderValue)
            .add(PAYMENT_SESSION_HEADER, paymentSession).build()
}
