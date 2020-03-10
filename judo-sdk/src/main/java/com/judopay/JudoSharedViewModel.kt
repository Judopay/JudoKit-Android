package com.judopay

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

class JudoSharedViewModel : ViewModel() {
    val paymentResult = MutableLiveData<JudoPaymentResult>()
}
