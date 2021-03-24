package com.judopay.judokit.android.api.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Consumer(
    var consumerToken: String? = null,
    val yourConsumerReference: String?
) : Parcelable
