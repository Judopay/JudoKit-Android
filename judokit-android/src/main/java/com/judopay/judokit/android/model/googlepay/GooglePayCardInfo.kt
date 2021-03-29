package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayCardInfo(
    val cardDetails: String,
    val cardNetwork: String,
    val billingAddress: GooglePayAddress?
) : Parcelable
