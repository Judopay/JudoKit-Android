package com.judopay.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayPaymentMethod(
    val type: GooglePayPaymentMethodType,
    val parameters: GooglePayCardParameters,
    val tokenizationSpecification: GooglePayPaymentMethodTokenizationSpecification?
) : Parcelable
