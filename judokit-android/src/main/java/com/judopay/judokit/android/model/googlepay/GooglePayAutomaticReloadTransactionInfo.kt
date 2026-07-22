package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayAutomaticReloadTransactionInfo(
    val currencyCode: String,
    val countryCode: String,
    val transactionId: String?,
    val tokenUpdateUrl: String?,
    val managementUrl: String?,
    val billingAgreement: String?,
    val immediateTotalPrice: String,
    val minimumBalanceAmount: String,
    val reloadAmount: String,
    val label: String,
) : Parcelable
