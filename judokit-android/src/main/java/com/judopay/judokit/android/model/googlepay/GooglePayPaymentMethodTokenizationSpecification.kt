package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayPaymentMethodTokenizationSpecification(
    val type: GooglePayTokenizationSpecificationType,
    val parameters: GPayPaymentGatewayParameters
) : Parcelable
