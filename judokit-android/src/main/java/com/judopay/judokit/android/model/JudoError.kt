package com.judopay.judokit.android.model

import android.content.res.Resources
import android.os.Parcelable
import com.judopay.judokit.android.R
import kotlinx.parcelize.Parcelize

const val USER_CANCELLED = -1
const val GOOGLE_PAY_NOT_SUPPORTED = -3
const val REQUEST_FAILED = -4
const val EXCEPTION_CAUGHT = -5
const val NETWORK_ERROR = -6

internal const val USER_CANCELLED_MSG =
    "The transaction was cancelled by the user. The user closed the transaction flow without completing the transaction."

/**
 * Error class that stores code, message and an optional list of details of the underlying error
 * that was caught in the SDK.
 */
@Parcelize
data class JudoError(
    var code: Int = USER_CANCELLED,
    var message: String = USER_CANCELLED_MSG,
    var details: MutableList<JudoError> = mutableListOf(),
) : Parcelable {
    /**
     * Utility functions to create specific JudoError objects according to the
     * prompted error. Requires [Resources] as parameter to translate the messages
     * when necessary.
     */
    companion object {
        /**
         * Utility function that creates a JudoError object with user cancelled
         * error code and message.
         */
        fun userCancelled() = JudoError(USER_CANCELLED, USER_CANCELLED_MSG)

        /**
         * Utility function that creates a JudoError object with request failed
         * error code and message. Used when the server responded with no data or error.
         */
        fun judoRequestFailedError(resources: Resources): JudoError =
            JudoError(
                REQUEST_FAILED,
                resources.getString(R.string.jp_error_request_failed_desc),
                mutableListOf(
                    JudoError(
                        REQUEST_FAILED,
                        resources.getString(R.string.jp_error_request_failed_reason),
                    ),
                ),
            )

        /**
         * Utility function that creates a JudoError object with GooglePay not supported
         * error code and message.
         */
        fun googlePayNotSupported(
            resources: Resources,
            message: String?,
        ) = JudoError(
            GOOGLE_PAY_NOT_SUPPORTED,
            resources.getString(R.string.jp_error_google_pay_not_supported_desc),
            mutableListOf(
                JudoError(
                    GOOGLE_PAY_NOT_SUPPORTED,
                    message ?: resources.getString(R.string.jp_error_google_pay_not_supported_reason),
                ),
            ),
        )

        /**
         * Utility function that creates a JudoError object with network
         * error code and message. Used when the network fails.
         */
        fun judoNetworkError(
            resources: Resources,
            throwable: Throwable,
        ): JudoError =
            JudoError(
                NETWORK_ERROR,
                resources.getString(R.string.jp_error_network_failed_desc),
                mutableListOf(
                    JudoError(
                        NETWORK_ERROR,
                        throwable.localizedMessage ?: resources.getString(R.string.jp_error_network_failed_reason),
                    ),
                ),
            )

        /**
         * Utility function that creates a JudoError object with poor internet connectivity
         * error code and message. Used when a SocketTimeoutException is thrown.
         */
        fun judoPoorConnectivityError(
            resources: Resources,
            throwable: Throwable,
        ): JudoError =
            JudoError(
                NETWORK_ERROR,
                resources.getString(R.string.jp_error_poor_connectivity_desc),
                mutableListOf(
                    JudoError(
                        NETWORK_ERROR,
                        throwable.localizedMessage ?: resources.getString(R.string.jp_error_poor_connectivity_reason),
                    ),
                ),
            )

        /**
         * Utility function that creates a JudoError object with thrown exception error code and message.
         */
        fun judoInternalError(message: String?) =
            JudoError(
                EXCEPTION_CAUGHT,
                message ?: "Unknown error",
            )

        fun judoRecommendationTransactionPreventedError(resources: Resources): JudoError =
            JudoError(
                REQUEST_FAILED,
                resources.getString(R.string.jp_recommendation_transaction_prevented_desc),
                mutableListOf(
                    JudoError(
                        REQUEST_FAILED,
                        resources.getString(R.string.jp_recommendation_transaction_prevented_reason),
                    ),
                ),
            )

        fun judoRecommendationRetrievingError(resources: Resources): JudoError =
            JudoError(
                REQUEST_FAILED,
                resources.getString(R.string.jp_recommendation_request_failed_desc),
                mutableListOf(
                    JudoError(
                        REQUEST_FAILED,
                        resources.getString(R.string.jp_recommendation_request_failed_reason),
                    ),
                ),
            )
    }
}
