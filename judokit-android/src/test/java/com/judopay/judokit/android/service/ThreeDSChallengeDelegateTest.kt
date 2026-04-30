package com.judopay.judokit.android.service

import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judo3ds2.transaction.challenge.ChallengeParameters
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@DisplayName("Testing ThreeDSChallengeDelegate")
internal class ThreeDSChallengeDelegateTest {
    private val transaction: Transaction = mockk(relaxed = true)
    private val params: ChallengeParameters = mockk(relaxed = true)

    @Test
    @DisplayName("Initial pendingChallenge is null")
    fun initialPendingChallengeIsNull() {
        val delegate = ThreeDSChallengeDelegate()
        assertNull(delegate.pendingChallenge.value)
    }

    @Test
    @DisplayName("challengeRunner sets pendingChallenge before suspending")
    fun challengeRunnerSetsPendingChallenge() =
        runTest(UnconfinedTestDispatcher()) {
            val delegate = ThreeDSChallengeDelegate()

            val job =
                launch {
                    delegate.challengeRunner(transaction, params)
                }

            val pending = delegate.pendingChallenge.value
            assertEquals(transaction, pending?.transaction)
            assertEquals(params, pending?.challengeParameters)

            delegate.onChallengeResult(null)
            job.join()
        }

    @Test
    @DisplayName("challengeRunner returns the result delivered by onChallengeResult")
    fun challengeRunnerReturnsOnChallengeResultValue() =
        runTest(UnconfinedTestDispatcher()) {
            val delegate = ThreeDSChallengeDelegate()
            var result: String? = "unset"

            val job =
                launch {
                    result = delegate.challengeRunner(transaction, params)
                }

            delegate.onChallengeResult("Completed|SDKTransactionID=123")
            job.join()

            assertEquals("Completed|SDKTransactionID=123", result)
        }

    @Test
    @DisplayName("onChallengeResult clears pendingChallenge")
    fun onChallengeResultClearsPendingChallenge() =
        runTest(UnconfinedTestDispatcher()) {
            val delegate = ThreeDSChallengeDelegate()

            val job =
                launch {
                    delegate.challengeRunner(transaction, params)
                }

            delegate.onChallengeResult("Cancelled")
            job.join()

            assertNull(delegate.pendingChallenge.value)
        }

    @Test
    @DisplayName("onChallengeResult with null status returns null from challengeRunner")
    fun onChallengeResultWithNull() =
        runTest(UnconfinedTestDispatcher()) {
            val delegate = ThreeDSChallengeDelegate()
            var result: String? = "unset"

            val job =
                launch {
                    result = delegate.challengeRunner(transaction, params)
                }

            delegate.onChallengeResult(null)
            job.join()

            assertNull(result)
        }

    @Test
    @DisplayName("Duplicate onChallengeResult is silently dropped; first result wins")
    fun duplicateOnChallengeResultIsDropped() =
        runTest(UnconfinedTestDispatcher()) {
            val delegate = ThreeDSChallengeDelegate()
            var result: String? = "unset"

            val job =
                launch {
                    result = delegate.challengeRunner(transaction, params)
                }

            delegate.onChallengeResult("first")
            delegate.onChallengeResult("second") // dropped — channel already has a value

            job.join()

            assertEquals("first", result)
        }

    @Test
    @DisplayName("onChallengeResult before challengeRunner: pending challenge stays null after result")
    fun pendingChallengeIsNullAfterResult() =
        runTest(UnconfinedTestDispatcher()) {
            val delegate = ThreeDSChallengeDelegate()

            launch {
                delegate.challengeRunner(transaction, params)
            }

            delegate.onChallengeResult("Timeout")
            advanceUntilIdle()

            assertNull(delegate.pendingChallenge.value)
        }
}
