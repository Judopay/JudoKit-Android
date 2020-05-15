package com.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayPaymentMethodTokenizationSpecification(
    val type: GooglePayTokenizationSpecificationType,
    val parameters: GPayPaymentGatewayParameters
) : Parcelable
