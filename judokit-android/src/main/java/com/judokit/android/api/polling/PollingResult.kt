package com.judokit.android.api.polling

import com.judopay.api.error.ApiError

sealed class PollingResult<out T> {
    object Delay : PollingResult<Nothing>()
    object Retry : PollingResult<Nothing>()
    object Processing : PollingResult<Nothing>()
    data class Failure(
        val statusCode: Int = -1,
        val error: ApiError? = null,
        val throwable: Throwable? = null
    ) : PollingResult<Nothing>()
    data class Success<T>(val data: T?) : PollingResult<T>()
}
