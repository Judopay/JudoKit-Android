package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayPaymentDataRequest(
    val apiVersion: Number,
    val apiVersionMinor: Number,
    val merchantInfo: GooglePayMerchantInfo?,
    val allowedPaymentMethods: Array<GooglePayPaymentMethod>,
    val transactionInfo: GooglePayTransactionInfo,
    val emailRequired: Boolean?,
    val shippingAddressRequired: Boolean?,
    val shippingAddressParameters: GooglePayShippingAddressParameters?
) : Parcelable
