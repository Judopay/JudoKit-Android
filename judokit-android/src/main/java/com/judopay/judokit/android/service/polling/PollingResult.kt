package com.judopay.judokit.android.service.polling

import com.judopay.judokit.android.api.error.ApiError

sealed class PollingResult<out T> {
    object Delay : PollingResult<Nothing>()

    object Retry : PollingResult<Nothing>()

    object Processing : PollingResult<Nothing>()

    object ResponseParseError : PollingResult<Nothing>()

    data class CallFailure(
        val statusCode: Int = -1,
        val error: ApiError? = null,
        val throwable: Throwable? = null,
    ) : PollingResult<Nothing>()

    data class Failure<T>(val data: T?) : PollingResult<T>()

    data class Success<T>(val data: T?) : PollingResult<T>()
}
