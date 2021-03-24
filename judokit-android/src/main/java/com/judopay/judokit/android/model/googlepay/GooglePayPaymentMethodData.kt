package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayPaymentMethodData(
    val type: String,
    val description: String,
    val info: GooglePayCardInfo,
    val tokenizationData: GooglePayPaymentMethodTokenizationData
) : Parcelable
