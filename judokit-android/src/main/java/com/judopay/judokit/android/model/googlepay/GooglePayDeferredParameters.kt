package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayDeferredParameters(
    val immediateTotalPrice: String,
    val billingDateTime: String,
    val priceStatus: GooglePayPriceStatus,
    val price: String? = null,
    val label: String,
    val immediateDisplayItems: List<GooglePayDisplayItem>? = null,
    val displayItems: List<GooglePayDisplayItem>? = null,
    val managementUrl: String? = null,
    val billingAgreement: String? = null,
) : Parcelable
