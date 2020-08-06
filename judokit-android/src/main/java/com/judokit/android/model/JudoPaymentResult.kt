package com.judokit.android.model

import android.content.Intent
import com.judokit.android.Judo.Companion.JUDO_ERROR
import com.judokit.android.Judo.Companion.JUDO_RESULT
import com.judokit.android.JudoActivity.Companion.RESULT_PAYMENT_CANCELLED
import com.judokit.android.JudoActivity.Companion.RESULT_PAYMENT_ERROR
import com.judokit.android.JudoActivity.Companion.RESULT_PAYMENT_SUCCESS

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
        is JudoPaymentResult.UserCancelled -> RESULT_PAYMENT_CANCELLED
        is JudoPaymentResult.Success -> RESULT_PAYMENT_SUCCESS
        is JudoPaymentResult.Error -> RESULT_PAYMENT_ERROR
    }
