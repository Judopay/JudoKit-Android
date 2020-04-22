package com.judopay.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

internal const val INTERNAL_ERROR = -2
internal const val USER_CANCELLED = -1

@Parcelize
data class JudoError(
    val code: Int,
    val message: String
) : Parcelable {
    companion object {
        fun userCancelled() = JudoError(USER_CANCELLED, "User cancelled")

        fun generic() = JudoError(INTERNAL_ERROR, "Oops! Something went wrong.")
    }
}
