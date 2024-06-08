package com.judopay.judokit.android.api.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Consumer(
    @Deprecated("Consumer Token is deprecated and will be removed in a future version.")
    var consumerToken: String? = null,
    val yourConsumerReference: String?,
) : Parcelable
