package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayDeferredTransactionInfo(
    val currencyCode: String,
    val countryCode: String,
    val transactionId: String?,
    val managementUrl: String?,
    val billingAgreement: String?,
    val immediateTotalPrice: String,
    val immediateDisplayItems: List<GooglePayDisplayItem>? = null,
    val billingDateTime: String,
    val priceStatus: GooglePayPriceStatus,
    val price: String?,
    val label: String,
    val displayItems: List<GooglePayDisplayItem>? = null,
) : Parcelable
