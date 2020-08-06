package com.judokit.android.model

import com.judokit.android.JudoActivity.Companion.RESULT_PAYMENT_CANCELLED
import com.judokit.android.JudoActivity.Companion.RESULT_PAYMENT_ERROR
import com.judokit.android.JudoActivity.Companion.RESULT_PAYMENT_SUCCESS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing JudoPaymentResult")
internal class JudoPaymentResultTest {

    @DisplayName("Given code is called, when result is UserCancelled, then return PAYMENT_CANCELLED")
    @Test
    fun returnPaymentCancelledOnCodeWithUserCancelled() {
        assertEquals(RESULT_PAYMENT_CANCELLED, JudoPaymentResult.UserCancelled().code)
    }

    @DisplayName("Given code is called, when result is Success, then return PAYMENT_SUCCESS")
    @Test
    fun returnPaymentSuccessOnCodeWithSuccess() {
        assertEquals(RESULT_PAYMENT_SUCCESS, JudoPaymentResult.Success(JudoResult()).code)
    }

    @DisplayName("Given code is called, when result is Error, then return PAYMENT_ERROR")
    @Test
    fun returnPaymentErrorOnCodeWithError() {
        assertEquals(RESULT_PAYMENT_ERROR, JudoPaymentResult.Error(JudoError()).code)
    }
}
