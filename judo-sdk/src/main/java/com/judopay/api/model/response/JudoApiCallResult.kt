package com.judopay.api.model.response

import com.judopay.api.error.ApiError

sealed class JudoApiCallResult<out T> {
    data class Success<T>(val data: T?) : JudoApiCallResult<T>()
    data class Failure(val statusCode: Int = -1,
                       val error: ApiError? = null,
                       val throwable: Throwable? = null) : JudoApiCallResult<Nothing>()
}
