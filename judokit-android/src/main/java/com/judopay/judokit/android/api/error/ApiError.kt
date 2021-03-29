package com.judopay.judokit.android.api.error

import com.judopay.judokit.android.model.JudoError

data class ApiError(
    val code: Int,
    val category: Int,
    val message: String,
    val details: List<ApiErrorDetail>? = emptyList()
) {
    override fun toString(): String {
        return "ApiError(code=$code, category=$category, message='$message', details=$details)"
    }
}

data class ApiErrorDetail(
    val code: Int,
    val message: String,
    val fieldName: String
) {
    override fun toString(): String {
        return "ApiErrorDetail(code=$code, message='$message', fieldName='$fieldName')"
    }
}

fun ApiError.toJudoError() = JudoError(code, message)
