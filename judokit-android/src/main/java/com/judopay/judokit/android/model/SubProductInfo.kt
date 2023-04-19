package com.judopay.judokit.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SubProductInfo : Parcelable {
    object Unknown : SubProductInfo()
    class ReactNative(val version: String) : SubProductInfo()
}
