package com.judokit.android.model

import android.content.res.Resources
import android.os.Parcelable
import com.judokit.android.R
import kotlinx.android.parcel.Parcelize

const val USER_CANCELLED = -1
const val RESPONSE_PARSING = -2
const val GOOGLE_PAY_NOT_SUPPORTED = -3
const val REQUEST_FAILED = -4

internal const val USER_CANCELLED_MSG = "The transaction was cancelled by the user."

@Parcelize
data class JudoError(
    var code: Int = USER_CANCELLED,
    var message: String = USER_CANCELLED_MSG,
    var details: MutableList<JudoError> = mutableListOf()
) : Parcelable {
    companion object {
        fun userCancelled(resources: Resources) = JudoError(
            USER_CANCELLED, resources.getString(R.string.error_user_cancelled_desc),
            mutableListOf(
                JudoError(
                    USER_CANCELLED,
                    resources.getString(R.string.error_user_cancelled_reason)
                )
            )
        )

        fun judoRequestFailedError(resources: Resources): JudoError = JudoError(
            REQUEST_FAILED,
            resources.getString(R.string.error_request_failed_desc),
            mutableListOf(
                JudoError(
                    REQUEST_FAILED,
                    resources.getString(R.string.error_request_failed_reason)
                )
            )
        )

        fun judoResponseParseError(resources: Resources) = JudoError(
            RESPONSE_PARSING, resources.getString(R.string.error_response_parse_desc),
            mutableListOf(
                JudoError(
                    RESPONSE_PARSING,
                    resources.getString(R.string.error_response_parse_reason)
                )
            )
        )

        fun googlePayNotSupported(resources: Resources, message: String?) = JudoError(
            GOOGLE_PAY_NOT_SUPPORTED,
            resources.getString(R.string.error_google_pay_not_supported_desc),
            mutableListOf(
                JudoError(
                    GOOGLE_PAY_NOT_SUPPORTED,
                    message ?: resources.getString(R.string.error_google_pay_not_supported_reason)
                )
            )
        )
    }
}
