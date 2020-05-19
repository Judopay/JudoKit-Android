package com.judokit.android.api.polling

sealed class PollingResult<out T> {
    data class Delay<T>(val data: T) : PollingResult<T>()
    data class Failure<T>(val data: T) : PollingResult<T>()
    data class Success<T>(val data: T) : PollingResult<T>()
}
