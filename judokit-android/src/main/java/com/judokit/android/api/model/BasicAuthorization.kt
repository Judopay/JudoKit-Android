package com.judokit.android.api.model

import android.util.Base64
import com.judokit.android.requireNotNullOrEmpty
import java.nio.charset.StandardCharsets
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import okhttp3.Headers

private const val AUTHORIZATION_HEADER = "Authorization"

@Parcelize
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
