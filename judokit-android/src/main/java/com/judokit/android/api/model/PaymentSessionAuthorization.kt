package com.judokit.android.api.model

import android.util.Base64
import com.judokit.android.api.interceptor.AUTHORIZATION_HEADER
import com.judokit.android.api.interceptor.PAYMENT_SESSION_HEADER
import com.judokit.android.requireNotNullOrEmpty
import java.nio.charset.StandardCharsets
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import okhttp3.Headers

@Parcelize
class PaymentSessionAuthorization(
    private val paymentSession: String?,
    private val apiToken: String?
) : Authorization {

    @IgnoredOnParcel
    private val paymentSessionHeaderValue = requireNotNullOrEmpty(paymentSession, "paymentSession")

    @IgnoredOnParcel
    private val myToken = requireNotNullOrEmpty(apiToken, "apiToken")

    @IgnoredOnParcel
    private val encodedCredentials: String =
        Base64.encodeToString("$myToken:".toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)

    private val authorizationHeaderValue: String
        get() = "Basic $encodedCredentials"

    private val authorizationHeaderName: String
        get() = AUTHORIZATION_HEADER

    private val paymentSessionHeaderName: String
        get() = PAYMENT_SESSION_HEADER

    override val headers: Headers
        get() = Headers.Builder().add(authorizationHeaderName, authorizationHeaderValue)
            .add(paymentSessionHeaderName, paymentSessionHeaderValue).build()
}
