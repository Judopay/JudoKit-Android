package com.judokit.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val INTERNAL_ERROR = -2
const val USER_CANCELLED = -1

private const val UNKNOWN_ERROR_MSG = "Oops! Something went wrong."
private const val USER_CANCELLED_MSG = "User cancelled"

@Parcelize
data class JudoError(
    var code: Int = USER_CANCELLED,
    var message: String = USER_CANCELLED_MSG,
    var details: MutableList<JudoError> = mutableListOf()
) : Parcelable {
    companion object {
        fun userCancelled(): JudoError {
            return JudoError(USER_CANCELLED, USER_CANCELLED_MSG)
        }

        fun generic(): JudoError {
            return JudoError(INTERNAL_ERROR, UNKNOWN_ERROR_MSG)
        }
    }
}
