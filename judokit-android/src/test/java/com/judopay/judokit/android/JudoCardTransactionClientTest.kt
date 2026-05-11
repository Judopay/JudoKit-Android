package com.judopay.judokit.android

import androidx.fragment.app.FragmentActivity
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.service.CardTransactionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@DisplayName("Testing JudoCardTransactionClient")
internal class JudoCardTransactionClientTest {
    private val repository: CardTransactionRepository = mockk(relaxed = true)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var sut: JudoCardTransactionClient

    private val activity: FragmentActivity = mockk(relaxed = true)
    private val details: TransactionDetails = mockk(relaxed = true)
    private val successResult = JudoPaymentResult.Success(JudoResult())

    @BeforeEach
    fun setUp() {
        sut = JudoCardTransactionClient(repository, testScope, UnconfinedTestDispatcher())
        coEvery { repository.payment(any(), any()) } returns successResult
        coEvery { repository.preAuth(any(), any()) } returns successResult
        coEvery { repository.paymentWithToken(any(), any()) } returns successResult
        coEvery { repository.preAuthWithToken(any(), any()) } returns successResult
        coEvery { repository.save(any(), any()) } returns successResult
        coEvery { repository.check(any(), any()) } returns successResult
    }

    @Nested
    @DisplayName("payment")
    inner class PaymentTests {
        @DisplayName("When payment is called, then repository.payment is invoked")
        @Test
        fun delegatesToRepositoryPayment() =
            runTest {
                sut.payment(activity, details) { }

                coVerify { repository.payment(details, any()) }
            }

        @DisplayName("When payment completes, then callback is invoked with the result")
        @Test
        fun invokesCallbackWithResult() =
            runTest {
                var callbackResult: JudoPaymentResult? = null
                sut.payment(activity, details) { callbackResult = it }

                assertEquals(successResult, callbackResult)
            }
    }

    @Nested
    @DisplayName("preAuth")
    inner class PreAuthTests {
        @DisplayName("When preAuth is called, then repository.preAuth is invoked")
        @Test
        fun delegatesToRepositoryPreAuth() =
            runTest {
                sut.preAuth(activity, details) { }

                coVerify { repository.preAuth(details, any()) }
            }

        @DisplayName("When preAuth completes, then callback is invoked with the result")
        @Test
        fun invokesCallbackWithResult() =
            runTest {
                var callbackResult: JudoPaymentResult? = null
                sut.preAuth(activity, details) { callbackResult = it }

                assertEquals(successResult, callbackResult)
            }
    }

    @Nested
    @DisplayName("paymentWithToken")
    inner class PaymentWithTokenTests {
        @DisplayName("When paymentWithToken is called, then repository.paymentWithToken is invoked")
        @Test
        fun delegatesToRepositoryPaymentWithToken() =
            runTest {
                sut.paymentWithToken(activity, details) { }

                coVerify { repository.paymentWithToken(details, any()) }
            }

        @DisplayName("When paymentWithToken completes, then callback is invoked with the result")
        @Test
        fun invokesCallbackWithResult() =
            runTest {
                var callbackResult: JudoPaymentResult? = null
                sut.paymentWithToken(activity, details) { callbackResult = it }

                assertEquals(successResult, callbackResult)
            }
    }

    @Nested
    @DisplayName("preAuthWithToken")
    inner class PreAuthWithTokenTests {
        @DisplayName("When preAuthWithToken is called, then repository.preAuthWithToken is invoked")
        @Test
        fun delegatesToRepositoryPreAuthWithToken() =
            runTest {
                sut.preAuthWithToken(activity, details) { }

                coVerify { repository.preAuthWithToken(details, any()) }
            }

        @DisplayName("When preAuthWithToken completes, then callback is invoked with the result")
        @Test
        fun invokesCallbackWithResult() =
            runTest {
                var callbackResult: JudoPaymentResult? = null
                sut.preAuthWithToken(activity, details) { callbackResult = it }

                assertEquals(successResult, callbackResult)
            }
    }

    @Nested
    @DisplayName("save")
    inner class SaveTests {
        @DisplayName("When save is called, then repository.save is invoked")
        @Test
        fun delegatesToRepositorySave() =
            runTest {
                sut.save(activity, details) { }

                coVerify { repository.save(details, any()) }
            }

        @DisplayName("When save completes, then callback is invoked with the result")
        @Test
        fun invokesCallbackWithResult() =
            runTest {
                var callbackResult: JudoPaymentResult? = null
                sut.save(activity, details) { callbackResult = it }

                assertEquals(successResult, callbackResult)
            }
    }

    @Nested
    @DisplayName("check")
    inner class CheckTests {
        @DisplayName("When check is called, then repository.check is invoked")
        @Test
        fun delegatesToRepositoryCheck() =
            runTest {
                sut.check(activity, details) { }

                coVerify { repository.check(details, any()) }
            }

        @DisplayName("When check completes, then callback is invoked with the result")
        @Test
        fun invokesCallbackWithResult() =
            runTest {
                var callbackResult: JudoPaymentResult? = null
                sut.check(activity, details) { callbackResult = it }

                assertEquals(successResult, callbackResult)
            }
    }
}
