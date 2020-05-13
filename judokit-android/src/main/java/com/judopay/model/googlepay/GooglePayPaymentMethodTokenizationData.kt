package com.judopay.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayPaymentMethodTokenizationData(
    val type: String,
    val token: String?
) : Parcelable
