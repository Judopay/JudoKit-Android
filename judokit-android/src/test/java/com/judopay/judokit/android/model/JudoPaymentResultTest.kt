package com.judopay.judokit.android.model

import com.judopay.judokit.android.PAYMENT_CANCELLED
import com.judopay.judokit.android.PAYMENT_ERROR
import com.judopay.judokit.android.PAYMENT_SUCCESS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing JudoPaymentResult")
internal class JudoPaymentResultTest {

    @DisplayName("Given code is called, when result is UserCancelled, then return PAYMENT_CANCELLED")
    @Test
    fun returnPaymentCancelledOnCodeWithUserCancelled() {
        assertEquals(PAYMENT_CANCELLED, JudoPaymentResult.UserCancelled().code)
    }

    @DisplayName("Given code is called, when result is Success, then return PAYMENT_SUCCESS")
    @Test
    fun returnPaymentSuccessOnCodeWithSuccess() {
        assertEquals(PAYMENT_SUCCESS, JudoPaymentResult.Success(JudoResult()).code)
    }

    @DisplayName("Given code is called, when result is Error, then return PAYMENT_ERROR")
    @Test
    fun returnPaymentErrorOnCodeWithError() {
        assertEquals(PAYMENT_ERROR, JudoPaymentResult.Error(JudoError()).code)
    }
}
