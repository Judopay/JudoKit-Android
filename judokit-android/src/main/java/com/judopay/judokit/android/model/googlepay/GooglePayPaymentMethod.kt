package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayPaymentMethod(
    val type: GooglePayPaymentMethodType,
    val parameters: GooglePayCardParameters,
    val tokenizationSpecification: GooglePayPaymentMethodTokenizationSpecification?
) : Parcelable
