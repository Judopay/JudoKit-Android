// Also covers the deprecated Context-based API.
@file:Suppress("DEPRECATION")

package com.judopay.judokit.android

import androidx.fragment.app.FragmentActivity
import com.judopay.judo3ds2.ui.challenge.ChallengeActivityResult
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.service.ThreeDSSDKChallengeStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

    @Nested
    @DisplayName("Activity/Fragment-bound client")
    inner class HostedTests {
        private val host: ChallengeHost = mockk(relaxed = true) { every { resolveSessionId() } returns "session-1" }
        private val delivered = mutableListOf<JudoPaymentResult>()
        private lateinit var hosted: JudoCardTransactionClient

        @BeforeEach
        fun setUpHosted() {
            hosted = JudoCardTransactionClient({ repository }, testScope, UnconfinedTestDispatcher()) { host }
            ChallengeSessions.attach("session-1", mockk(relaxed = true)) { delivered += it }
        }

        @AfterEach
        fun tearDownHosted() = ChallengeSessions.reset()

        @DisplayName("When payment is called, then the result is delivered to the session's receiver")
        @Test
        fun paymentDeliversToReceiver() =
            runTest {
                hosted.payment(details)

                coVerify { repository.payment(details, any()) }
                assertEquals(listOf<JudoPaymentResult>(successResult), delivered)
            }

        @DisplayName("Every transaction type delivers to the session's receiver")
        @Test
        fun allTransactionTypesDeliverToReceiver() =
            runTest {
                hosted.preAuth(details)
                hosted.paymentWithToken(details)
                hosted.preAuthWithToken(details)
                hosted.save(details)
                hosted.check(details)

                assertEquals(5, delivered.size)
            }

        @DisplayName("When a transaction is in progress, then another one is ignored")
        @Test
        fun secondTransactionIsIgnored() =
            runTest {
                val pending = CompletableDeferred<JudoPaymentResult>()
                coEvery { repository.payment(any(), any()) } coAnswers { pending.await() }

                hosted.payment(details)
                assertTrue(ChallengeSessions.isTransactionInProgress("session-1"))
                hosted.payment(details)

                coVerify(exactly = 1) { repository.payment(any(), any()) }
                pending.complete(successResult)
                assertFalse(ChallengeSessions.isTransactionInProgress("session-1"))
                assertEquals(listOf<JudoPaymentResult>(successResult), delivered)
            }

        @DisplayName("When the host was destroyed for good, then a transaction is rejected with an error")
        @Test
        fun retiredHostRejectsTransaction() =
            runTest {
                val rejected = mutableListOf<JudoPaymentResult>()
                every { host.isRetired } returns true
                every { host.receiver } returns JudoCardTransactionCallback { rejected += it }

                hosted.payment(details)

                coVerify(exactly = 0) { repository.payment(any(), any()) }
                assertTrue(rejected.single() is JudoPaymentResult.Error)
                assertFalse(ChallengeSessions.isTransactionInProgress("session-1"))
            }

        @DisplayName("When the transaction throws, then an error is delivered and the client is free again")
        @Test
        fun transactionFailureDeliversError() =
            runTest {
                coEvery { repository.payment(any(), any()) } throws IllegalStateException("boom")

                hosted.payment(details)

                assertTrue(delivered.single() is JudoPaymentResult.Error)
                assertFalse(ChallengeSessions.isTransactionInProgress("session-1"))
            }

        @DisplayName("When the repository cannot be created, then an error is delivered and the client is free again")
        @Test
        fun repositoryFailureDeliversError() =
            runTest {
                val failing = JudoCardTransactionClient({ error("no context") }, testScope, UnconfinedTestDispatcher()) { host }

                failing.payment(details)

                assertTrue(delivered.single() is JudoPaymentResult.Error)
                assertFalse(ChallengeSessions.isTransactionInProgress("session-1"))
            }

        @DisplayName("When the transaction is cancelled, then it is no longer in progress")
        @Test
        fun cancelledTransactionIsNotInProgress() =
            runTest {
                coEvery { repository.payment(any(), any()) } throws CancellationException("cancelled")

                hosted.payment(details)

                assertTrue(delivered.isEmpty())
                assertFalse(ChallengeSessions.isTransactionInProgress("session-1"))
            }

        @DisplayName("When a client created from a Context calls a hosted method, then it throws")
        @Test
        fun legacyClientRejectsHostedMethods() {
            assertThrows<IllegalStateException> { sut.payment(details) }
        }

        @DisplayName("An orphaned challenge outcome is completed via complete3ds2 without CV2")
        @Test
        fun orphanedChallengeIsCompleted() =
            runTest {
                coEvery { repository.complete3ds2("receipt-1", "2.2.0", null, ThreeDSSDKChallengeStatus.CANCELLED) } returns successResult

                hosted.completeOrphanedChallenge("session-1", PendingChallenge("receipt-1", "2.2.0"), ChallengeActivityResult.Cancelled)

                assertEquals(listOf<JudoPaymentResult>(successResult), delivered)
                assertFalse(ChallengeSessions.isTransactionInProgress("session-1"))
            }
    }
}
