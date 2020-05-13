package com.judopay.model.googlepay

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GooglePayMerchantInfo(
    val merchantName: String?
) : Parcelable
