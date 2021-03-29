package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayIsReadyToPayRequest(
    val apiVersion: Number,
    val apiVersionMinor: Number,
    val allowedPaymentMethods: Array<GooglePayPaymentMethod>,
    val existingPaymentMethodRequired: Boolean? = null
) : Parcelable
