package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

private const val JUDOPAY = "judopay"

@Parcelize
data class GPayPaymentGatewayParameters(
    val gateway: String = JUDOPAY,
    val gatewayMerchantId: String
) : Parcelable
