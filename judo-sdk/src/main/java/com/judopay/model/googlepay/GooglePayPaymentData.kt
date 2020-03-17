package com.judopay.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayPaymentData(
    val apiVersion: Number,
    val apiVersionMinor: Number,
    val paymentMethodData: GooglePayPaymentMethodData,
    val email: String?,
    val shippingAddress: GooglePayAddress?
) : Parcelable
