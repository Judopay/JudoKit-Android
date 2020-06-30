package com.judokit.android.api.model

import android.util.Base64
import com.judokit.android.api.interceptor.AUTHORIZATION_HEADER
import com.judokit.android.requireNotNullOrEmpty
import java.nio.charset.StandardCharsets
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import okhttp3.Headers

@Parcelize
class BasicAuthorization(private val apiToken: String?, private val apiSecret: String?) :
    Authorization {

    @IgnoredOnParcel
    private val myToken = requireNotNullOrEmpty(apiToken, "apiToken")

    @IgnoredOnParcel
    private val mySecret = requireNotNullOrEmpty(apiSecret, "apiSecret")

    @IgnoredOnParcel
    private val encodedCredentials: String =
        Base64.encodeToString(
            "$myToken:$mySecret".toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP
        )

    private val authorizationHeaderValue: String
        get() = "Basic $encodedCredentials"

    private val authorizationHeaderName: String
        get() = AUTHORIZATION_HEADER

    override val headers: Headers
        get() = Headers.Builder().add(authorizationHeaderName, authorizationHeaderValue).build()
}
