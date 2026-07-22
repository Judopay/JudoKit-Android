package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayAutomaticReloadParameters(
    val immediateTotalPrice: String,
    val minimumBalanceAmount: String,
    val reloadAmount: String,
    val label: String,
    val tokenUpdateUrl: String? = null,
    val managementUrl: String? = null,
    val billingAgreement: String? = null,
) : Parcelable
