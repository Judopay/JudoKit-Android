package com.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayTransactionInfo(
    val currencyCode: String,
    val countryCode: String?,
    val transactionId: String?,
    val totalPriceStatus: GooglePayPriceStatus,
    val totalPrice: String?,
    val totalPriceLabel: String?,
    val checkoutOption: GooglePayCheckoutOption?
) : Parcelable
