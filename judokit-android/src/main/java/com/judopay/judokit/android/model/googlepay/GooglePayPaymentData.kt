package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayPaymentData(
    val apiVersion: Number,
    val apiVersionMinor: Number,
    val paymentMethodData: GooglePayPaymentMethodData,
    val email: String?,
    val shippingAddress: GooglePayAddress?,
) : Parcelable
