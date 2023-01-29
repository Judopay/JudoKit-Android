package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayPaymentMethodTokenizationData(
    val type: String,
    val token: String?
) : Parcelable
