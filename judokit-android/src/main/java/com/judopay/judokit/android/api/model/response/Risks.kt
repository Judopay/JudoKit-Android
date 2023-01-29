package com.judopay.judokit.android.api.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Risks(val postCodeCheck: String? = null) : Parcelable {
    override fun toString(): String {
        return "Risks(postCodeCheck=$postCodeCheck)"
    }
}
