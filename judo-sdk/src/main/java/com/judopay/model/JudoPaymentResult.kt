package com.judopay.model

import android.content.Intent
import com.judopay.JUDO_ERROR
import com.judopay.JUDO_RECEIPT
import com.judopay.PAYMENT_CANCELLED
import com.judopay.PAYMENT_ERROR
import com.judopay.PAYMENT_SUCCESS
import com.judopay.api.error.ApiError
import com.judopay.api.model.response.Receipt

sealed class JudoPaymentResult {
    data class Success(val receipt: Receipt) : JudoPaymentResult()
    data class Error(val error: ApiError) : JudoPaymentResult()
    object UserCancelled : JudoPaymentResult()
}

fun JudoPaymentResult.toIntent(): Intent {
    val intent = Intent()

    when (this) {
        is JudoPaymentResult.UserCancelled -> {
            // TODO: to rethink this
            intent.putExtra(JUDO_ERROR, ApiError(-1, -1, "User cancelled"))
        }

        is JudoPaymentResult.Error -> {
            intent.putExtra(JUDO_ERROR, error)
        }

        is JudoPaymentResult.Success -> {
            intent.putExtra(JUDO_RECEIPT, receipt)
        }
    }
    return intent
}

val JudoPaymentResult.code: Int
    get() = when (this) {
        is JudoPaymentResult.UserCancelled -> PAYMENT_CANCELLED
        is JudoPaymentResult.Success -> PAYMENT_SUCCESS
        is JudoPaymentResult.Error -> PAYMENT_ERROR
    }