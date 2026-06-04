package com.judopay.judokit.android

import android.content.Intent
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Testing JudoActivityResultContracts.Base.parseResult")
internal class JudoActivityResultContractsTest {
    private val sut = JudoActivityResultContracts.CardPayment()

    @Nested
    @DisplayName("Given resultCode is PAYMENT_SUCCESS")
    inner class PaymentSuccessTests {
        @DisplayName("When intent contains a valid JudoResult, then return JudoPaymentResult.Success")
        @Test
        fun returnSuccessWhenJudoResultPresent() {
            val judoResult = JudoResult()
            val intent =
                mockk<Intent>(relaxed = true) {
                    @Suppress("DEPRECATION")
                    every { getParcelableExtra<JudoResult>(JUDO_RESULT) } returns judoResult
                }

            val result = sut.parseResult(PAYMENT_SUCCESS, intent)

            assertTrue(result is JudoPaymentResult.Success)
            assertEquals(judoResult, (result as JudoPaymentResult.Success).result)
        }

        @DisplayName("When intent JudoResult is null, then return JudoPaymentResult.Error with internal error")
        @Test
        fun returnErrorWhenJudoResultMissing() {
            val intent =
                mockk<Intent>(relaxed = true) {
                    @Suppress("DEPRECATION")
                    every { getParcelableExtra<JudoResult>(JUDO_RESULT) } returns null
                }

            val result = sut.parseResult(PAYMENT_SUCCESS, intent)

            assertTrue(result is JudoPaymentResult.Error)
        }

        @DisplayName("When intent is null, then return JudoPaymentResult.Error with internal error")
        @Test
        fun returnErrorWhenIntentIsNull() {
            val result = sut.parseResult(PAYMENT_SUCCESS, null)

            assertTrue(result is JudoPaymentResult.Error)
        }
    }

    @Nested
    @DisplayName("Given resultCode is PAYMENT_ERROR")
    inner class PaymentErrorTests {
        @DisplayName("When intent contains a valid JudoError, then return JudoPaymentResult.Error with that error")
        @Test
        fun returnErrorWithJudoErrorWhenPresent() {
            val judoError = JudoError.userCancelled()
            val intent =
                mockk<Intent>(relaxed = true) {
                    @Suppress("DEPRECATION")
                    every { getParcelableExtra<JudoError>(JUDO_ERROR) } returns judoError
                }

            val result = sut.parseResult(PAYMENT_ERROR, intent)

            assertTrue(result is JudoPaymentResult.Error)
            assertEquals(judoError, (result as JudoPaymentResult.Error).error)
        }

        @DisplayName("When intent JudoError is null, then return JudoPaymentResult.Error with internal error")
        @Test
        fun returnInternalErrorWhenJudoErrorMissing() {
            val intent =
                mockk<Intent>(relaxed = true) {
                    @Suppress("DEPRECATION")
                    every { getParcelableExtra<JudoError>(JUDO_ERROR) } returns null
                }

            val result = sut.parseResult(PAYMENT_ERROR, intent)

            assertTrue(result is JudoPaymentResult.Error)
        }
    }

    @Nested
    @DisplayName("Given resultCode is RESULT_CANCELED (or any other non-success/error code)")
    inner class CancelledTests {
        @DisplayName("When intent contains a valid JudoError, then return JudoPaymentResult.UserCancelled with that error")
        @Test
        fun returnUserCancelledWithJudoErrorWhenPresent() {
            val judoError = JudoError.userCancelled()
            val intent =
                mockk<Intent>(relaxed = true) {
                    @Suppress("DEPRECATION")
                    every { getParcelableExtra<JudoError>(JUDO_ERROR) } returns judoError
                }

            val result = sut.parseResult(android.app.Activity.RESULT_CANCELED, intent)

            assertTrue(result is JudoPaymentResult.UserCancelled)
            assertEquals(judoError, (result as JudoPaymentResult.UserCancelled).error)
        }

        @DisplayName("When intent JudoError is null, then return JudoPaymentResult.UserCancelled with default error")
        @Test
        fun returnUserCancelledWithDefaultErrorWhenJudoErrorMissing() {
            val intent =
                mockk<Intent>(relaxed = true) {
                    @Suppress("DEPRECATION")
                    every { getParcelableExtra<JudoError>(JUDO_ERROR) } returns null
                }

            val result = sut.parseResult(android.app.Activity.RESULT_CANCELED, intent)

            assertTrue(result is JudoPaymentResult.UserCancelled)
        }

        @DisplayName("When intent is null, then return JudoPaymentResult.UserCancelled with default error")
        @Test
        fun returnUserCancelledWithDefaultErrorWhenIntentIsNull() {
            val result = sut.parseResult(android.app.Activity.RESULT_CANCELED, null)

            assertTrue(result is JudoPaymentResult.UserCancelled)
        }

        @DisplayName("When resultCode is unknown, then return JudoPaymentResult.UserCancelled")
        @Test
        fun returnUserCancelledForUnknownResultCode() {
            val result = sut.parseResult(999, null)

            assertTrue(result is JudoPaymentResult.UserCancelled)
        }
    }
}
