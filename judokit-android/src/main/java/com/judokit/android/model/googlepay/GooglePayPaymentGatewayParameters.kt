package com.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

private const val JUDOPAY = "judopay"

@Parcelize
data class GPayPaymentGatewayParameters(
    val gateway: String = JUDOPAY,
    val gatewayMerchantId: String
) : Parcelable
