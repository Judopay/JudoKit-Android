package com.judopay.judokit.android.api.model.response

import android.content.res.Resources
import com.judopay.judokit.android.api.error.ApiError
import com.judopay.judokit.android.api.error.toJudoError
import com.judopay.judokit.android.api.exception.NetworkConnectivityException
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import java.net.SocketTimeoutException

sealed class JudoApiCallResult<out T> {
    data class Success<T>(val data: T?) : JudoApiCallResult<T>()
    data class Failure(
        val statusCode: Int = -1,
        val error: ApiError? = null,
        val throwable: Throwable? = null
    ) : JudoApiCallResult<Nothing>()
}

inline fun <reified T> JudoApiCallResult<T>.toJudoPaymentResult(resources: Resources): JudoPaymentResult {
    val fallbackError = JudoError.judoRequestFailedError(resources)

    return when (this) {
        is JudoApiCallResult.Success ->
            if (data != null) {
                when (T::class) {
                    Receipt::class ->
                        JudoPaymentResult.Success((data as Receipt).toJudoResult())
                    RecommendationResponse::class ->
                        JudoPaymentResult.Success((data as RecommendationResponse).toJudoResult())
                    else -> JudoPaymentResult.Error(fallbackError)
                }
            } else {
                JudoPaymentResult.Error(fallbackError)
            }
        is JudoApiCallResult.Failure ->
            when (throwable) {
                is NetworkConnectivityException ->
                    JudoPaymentResult.Error(JudoError.judoNetworkError(resources, throwable))
                is SocketTimeoutException ->
                    JudoPaymentResult.Error(JudoError.judoPoorConnectivityError(resources, throwable))
                else ->
                    JudoPaymentResult.Error(error?.toJudoError() ?: fallbackError)
            }
    }
}