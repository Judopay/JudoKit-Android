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
    val billingDateTime: String,
    val priceStatus: GooglePayPriceStatus,
    val price: String?,
    val label: String,
) : Parcelable
