package com.judopay.api.model.response

sealed class JudoApiCallResult<out T> {
    data class Success<T>(val data: T?) : JudoApiCallResult<T>()
    data class Failure(val statusCode: Int?) : JudoApiCallResult<Nothing>()
    data class NetworkError(val error: Throwable) : JudoApiCallResult<Nothing>()
}
