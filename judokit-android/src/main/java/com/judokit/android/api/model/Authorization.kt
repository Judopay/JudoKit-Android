package com.judokit.android.api.model

import android.os.Parcelable
import okhttp3.Headers

/**
 * Creating a Judo configuration object requires an Authorization implementation.
 * Available authorization methods are [BasicAuthorization] and [PaymentSessionAuthorization].
 * If you use token and secret combination - use [BasicAuthorization].
 * If you use api token and one time payment session token - use [PaymentSessionAuthorization]
 * @property headers Adds encrypted authorization credentials to header in every request.
 */
interface Authorization : Parcelable {
    val headers: Headers
}
