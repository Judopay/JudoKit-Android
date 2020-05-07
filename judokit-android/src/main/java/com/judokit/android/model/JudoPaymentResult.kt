package com.judopay.model

import android.content.Intent
import com.judopay.JUDO_ERROR
import com.judopay.JUDO_RESULT
import com.judopay.PAYMENT_CANCELLED
import com.judopay.PAYMENT_ERROR
import com.judopay.PAYMENT_SUCCESS

sealed class JudoPaymentResult {
    data class Success(val result: JudoResult) : JudoPaymentResult()
    data class Error(val error: JudoError) : JudoPaymentResult()
    data class UserCancelled(var error: JudoError = JudoError.userCancelled()) : JudoPaymentResult()
}

fun JudoPaymentResult.toIntent(): Intent {
    val intent = Intent()

    when (this) {
        is JudoPaymentResult.UserCancelled -> {
            // TODO: to rethink this
            intent.putExtra(JUDO_ERROR, error)
        }

        is JudoPaymentResult.Error -> {
            intent.putExtra(JUDO_ERROR, error)
        }

        is JudoPaymentResult.Success -> {
            intent.putExtra(JUDO_RESULT, result)
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
