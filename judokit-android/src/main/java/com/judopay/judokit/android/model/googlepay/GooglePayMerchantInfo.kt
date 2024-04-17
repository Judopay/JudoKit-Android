package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayMerchantInfo(
    val merchantName: String?,
) : Parcelable
