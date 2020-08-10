package com.judokit.android.api.model.response

import com.judokit.android.api.error.ApiError
import com.judokit.android.api.error.toJudoError
import com.judokit.android.model.JudoError
import com.judokit.android.model.JudoPaymentResult

sealed class JudoApiCallResult<out T> {
    data class Success<T>(val data: T?) : JudoApiCallResult<T>()
    data class Failure(
        val statusCode: Int = -1,
        val error: ApiError? = null,
        val throwable: Throwable? = null
    ) : JudoApiCallResult<Nothing>()
}

fun JudoApiCallResult<Receipt>.toJudoPaymentResult(): JudoPaymentResult {
    val fallbackError = JudoError.generic()

    return when (this) {
        is JudoApiCallResult.Success ->
            if (data != null) {
                JudoPaymentResult.Success(data.toJudoResult())
            } else {
                JudoPaymentResult.Error(fallbackError)
            }
        is JudoApiCallResult.Failure -> JudoPaymentResult.Error(error?.toJudoError() ?: fallbackError)
    }
}
