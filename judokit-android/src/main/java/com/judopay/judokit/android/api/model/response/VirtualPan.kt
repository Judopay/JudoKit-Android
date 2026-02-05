package com.judopay.judokit.android.api.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VirtualPan(
    val lastFour: String? = null,
    val expiryDate: String? = null,
) : Parcelable {
    override fun toString(): String {
        return "VirtualPan(lastFour=$lastFour, expiryDate=$expiryDate)"
    }
}
