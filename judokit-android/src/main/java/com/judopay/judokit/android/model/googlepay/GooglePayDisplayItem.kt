package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GooglePayDisplayItemType : Parcelable {
    DISCOUNT,
    LINE_ITEM,
    SHIPPING_OPTION,
    SUBTOTAL,
    TAX,
}

@Parcelize
enum class GooglePayDisplayItemStatus : Parcelable {
    FINAL,
    PENDING,
}

@Parcelize
data class GooglePayDisplayItem(
    val label: String,
    val type: GooglePayDisplayItemType,
    val price: String,
    val status: GooglePayDisplayItemStatus? = null,
) : Parcelable
