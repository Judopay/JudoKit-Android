package com.judokit.android.service.polling

import com.judokit.android.api.error.ApiError

sealed class PollingResult<out T> {
    data class Delay<T>(val data: T?) : PollingResult<T>()
    data class Retry<T>(val data: T?) : PollingResult<T>()
    data class Processing<T>(val data: T?) : PollingResult<T>()
    data class Failure(
        val statusCode: Int = -1,
        val error: ApiError? = null,
        val throwable: Throwable? = null
    ) : PollingResult<Nothing>()
    data class Success<T>(val data: T?) : PollingResult<T>()
}
