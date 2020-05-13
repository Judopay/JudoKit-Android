package com.judopay.api.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Risks(val postCodeCheck: String? = null) : Parcelable {
    override fun toString(): String {
        return "Risks(postCodeCheck=$postCodeCheck)"
    }
}
