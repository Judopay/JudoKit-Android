package com.judopay.judokit.android.api.model

import android.util.Base64
import com.judopay.judokit.android.requireNotNullOrEmpty
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import okhttp3.Headers
import java.nio.charset.StandardCharsets

private const val AUTHORIZATION_HEADER = "Authorization"

/**
 * Authorization type that uses token and secret combination to authorize Judo backend requests.
 * @param apiToken Token provided by JudoPay.
 * @param apiSecret Secret provided by JudoPay.
 */
@Parcelize
@Deprecated("This authentication method is deprecated, please use payment session instead.", ReplaceWith("PaymentSessionAuthorization"))
class BasicAuthorization internal constructor(
    private val apiToken: String,
    private val apiSecret: String
) :
    Authorization {

    class Builder {
        private var apiToken: String? = null
        private var apiSecret: String? = null

        fun setApiToken(apiToken: String?) = apply { this.apiToken = apiToken }
        fun setApiSecret(apiSecret: String?) = apply { this.apiSecret = apiSecret }

        fun build(): BasicAuthorization {
            val myToken = requireNotNullOrEmpty(apiToken, "apiToken")
            val mySecret = requireNotNullOrEmpty(apiSecret, "apiSecret")

            return BasicAuthorization(myToken, mySecret)
        }
    }

    @IgnoredOnParcel
    private val encodedCredentials: String =
        Base64.encodeToString(
            "$apiToken:$apiSecret".toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP
        )

    private val authorizationHeaderValue: String
        get() = "Basic $encodedCredentials"

    override val headers: Headers
        get() = Headers.Builder().add(AUTHORIZATION_HEADER, authorizationHeaderValue).build()
}
