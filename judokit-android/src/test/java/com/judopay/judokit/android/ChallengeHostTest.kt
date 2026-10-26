package com.judopay.judokit.android

import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import com.judopay.judo3ds2.exception.SDKRuntimeException
import com.judopay.judo3ds2.ui.challenge.ChallengeActivityResult
import com.judopay.judo3ds2.ui.challenge.model.ChallengeExtra
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.service.ChallengeData
import com.judopay.judokit.android.service.ThreeDSSDKChallengeStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private const val SAVED_STATE_KEY = "com.judopay.judokit.android.ChallengeHost"

/** A [Bundle] backed by a map: unit tests run against the Android stub, whose Bundle stores nothing. */
private fun fakeBundle(vararg entries: Pair<String, String>): Bundle {
    val values = mutableMapOf<String, String?>(*entries)
    return mockk {
        every { putString(any(), any()) } answers { values[firstArg()] = secondArg() }
        every { getString(any()) } answers { values[firstArg()] }
    }
}

@ExperimentalCoroutinesApi
@DisplayName("Testing ChallengeHost")
internal class ChallengeHostTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val extra: ChallengeExtra = mockk(relaxed = true)
    private val data: ChallengeData =
        mockk {
            every { toChallengeExtra() } returns extra
            every { receiptId } returns "receipt-1"
            every { messageVersion } returns "2.2.0"
        }

    /** A fake Activity/Fragment: captures its launcher callback, saved state provider and lifecycle observer. */
    private inner class FakeHost(
        restoredState: Bundle? = null,
        private val hasReceiver: Boolean = true,
    ) {
        val launcher: ActivityResultLauncher<ChallengeExtra> = mockk(relaxed = true)
        val resultCallback = slot<ActivityResultCallback<ChallengeActivityResult>>()
        val savedStateProvider = slot<SavedStateRegistry.SavedStateProvider>()
        val lifecycleObserver = slot<LifecycleEventObserver>()
        val results = mutableListOf<JudoPaymentResult>()
        val orphaned = mutableListOf<Pair<PendingChallenge, ChallengeActivityResult>>()
        var isFinishing = false

        private val caller: ActivityResultCaller =
            mockk {
                every {
                    registerForActivityResult(
                        any<ActivityResultContract<ChallengeExtra, ChallengeActivityResult>>(),
                        capture(resultCallback),
                    )
                } returns launcher
            }
        private val registry: SavedStateRegistry =
            mockk(relaxed = true) {
                every { isRestored } returns true
                every { consumeRestoredStateForKey(SAVED_STATE_KEY) } returns restoredState
                every { registerSavedStateProvider(SAVED_STATE_KEY, capture(savedStateProvider)) } returns Unit
            }
        private val lifecycle: Lifecycle =
            mockk(relaxed = true) {
                every { addObserver(capture(lifecycleObserver)) } returns Unit
            }
        private val owner: SavedStateRegistryOwner =
            mockk {
                every { savedStateRegistry } returns registry
                every { this@mockk.lifecycle } returns this@FakeHost.lifecycle
            }

        val host =
            ChallengeHost(
                caller,
                owner,
                { isFinishing },
                if (hasReceiver) JudoCardTransactionCallback { results += it } else null,
                { _, pending, result -> orphaned += pending to result },
                ::fakeBundle,
            )

        val sessionId get() = host.resolveSessionId()

        val runChallenge get() = ChallengeSessions.runner(sessionId, dispatcher)

        fun dispatch(event: Lifecycle.Event) = lifecycleObserver.captured.onStateChanged(mockk<LifecycleOwner>(), event)

        fun challengeEnds(result: ChallengeActivityResult) = resultCallback.captured.onActivityResult(result)

        fun saveState(): Bundle = savedStateProvider.captured.saveState()

        fun createAndStart() =
            apply {
                dispatch(Lifecycle.Event.ON_CREATE)
                dispatch(Lifecycle.Event.ON_START)
            }

        /** Destroys this instance as a configuration change would, returning its replacement, started. */
        fun recreate(): FakeHost {
            val saved = saveState()
            dispatch(Lifecycle.Event.ON_STOP)
            dispatch(Lifecycle.Event.ON_DESTROY)
            return FakeHost(restoredState = saved, hasReceiver = hasReceiver).createAndStart()
        }
    }

    private fun startedHost() = FakeHost().createAndStart()

    /** A fake Fragment host whose own removal, and its parent's, the test controls. */
    private class FakeFragment(
        parent: Fragment? = null,
    ) {
        private val lifecycleObserver = slot<LifecycleEventObserver>()
        private val savedStateProvider = slot<SavedStateRegistry.SavedStateProvider>()
        var isRemoving = false

        private val fragment: Fragment =
            mockk {
                every {
                    registerForActivityResult(
                        any<ActivityResultContract<ChallengeExtra, ChallengeActivityResult>>(),
                        any<ActivityResultCallback<ChallengeActivityResult>>(),
                    )
                } returns mockk(relaxed = true)
                every { savedStateRegistry } returns
                    mockk(relaxed = true) {
                        every { isRestored } returns false
                        every { registerSavedStateProvider(SAVED_STATE_KEY, capture(savedStateProvider)) } returns Unit
                    }
                every { lifecycle } returns mockk { every { addObserver(capture(lifecycleObserver)) } returns Unit }
                every { this@mockk.isRemoving } answers { this@FakeFragment.isRemoving }
                every { parentFragment } returns parent
                every { activity } returns mockk { every { isFinishing } returns false }
            }

        val host = ChallengeHost.of(fragment, receiver = null, onOrphanedResult = { _, _, _ -> }, newBundle = ::fakeBundle)

        fun dispatch(event: Lifecycle.Event) = lifecycleObserver.captured.onStateChanged(mockk(), event)

        fun saveState(): Bundle = savedStateProvider.captured.saveState()
    }

    @BeforeEach
    fun setUp() = ChallengeSessions.reset()

    @AfterEach
    fun tearDown() = ChallengeSessions.reset()

    @Test
    @DisplayName("The runner launches the challenge extra and returns the formatted outcome")
    fun runnerReturnsOutcome() =
        runTest(dispatcher) {
            val fake = startedHost()

            val status = async { fake.runChallenge(data) }
            verify { fake.launcher.launch(extra) }
            fake.challengeEnds(ChallengeActivityResult.Cancelled)

            assertEquals(ThreeDSSDKChallengeStatus.CANCELLED, status.await())
        }

    @Test
    @DisplayName("When launching throws, the runner returns a RuntimeError status")
    fun runnerReturnsRuntimeErrorWhenLaunchFails() =
        runTest(dispatcher) {
            val fake = startedHost()
            every { fake.launcher.launch(any()) } throws SDKRuntimeException("The transaction has already completed.")

            val status = fake.runChallenge(data)

            assertTrue(checkNotNull(status).startsWith(ThreeDSSDKChallengeStatus.RUNTIME_ERROR))
        }

    @Test
    @DisplayName("A runner obtained before the host is recreated launches through the recreated host")
    fun runnerLaunchesThroughCurrentHost() =
        runTest(dispatcher) {
            val original = startedHost()
            val runChallenge = original.runChallenge

            val recreated = original.recreate()
            backgroundScope.async { runChallenge(data) }

            verify(exactly = 0) { original.launcher.launch(any()) }
            verify { recreated.launcher.launch(extra) }
        }

    @Test
    @DisplayName("A challenge started while no host is created is launched by the next one")
    fun challengeWaitsForNextHost() =
        runTest(dispatcher) {
            val original = startedHost()
            val saved = original.saveState()
            original.dispatch(Lifecycle.Event.ON_DESTROY)

            val status = async { original.runChallenge(data) }
            verify(exactly = 0) { original.launcher.launch(any()) }

            val recreated = FakeHost(restoredState = saved).apply { dispatch(Lifecycle.Event.ON_CREATE) }
            verify(exactly = 0) { recreated.launcher.launch(any()) }
            recreated.dispatch(Lifecycle.Event.ON_START)
            verify { recreated.launcher.launch(extra) }
            recreated.challengeEnds(ChallengeActivityResult.TimedOut)

            assertEquals(ThreeDSSDKChallengeStatus.TIMEOUT, status.await())
        }

    @Test
    @DisplayName("A challenge started while the host is stopped is launched once it is started again")
    fun challengeWaitsWhileHostStopped() =
        runTest(dispatcher) {
            val fake = startedHost()
            fake.dispatch(Lifecycle.Event.ON_STOP)

            val status = async { fake.runChallenge(data) }
            verify(exactly = 0) { fake.launcher.launch(any()) }

            fake.dispatch(Lifecycle.Event.ON_START)
            verify(exactly = 1) { fake.launcher.launch(extra) }
            fake.challengeEnds(ChallengeActivityResult.Cancelled)

            assertEquals(ThreeDSSDKChallengeStatus.CANCELLED, status.await())
        }

    @Test
    @DisplayName("A host recreated in the same process delivers the outcome to the still-waiting coroutine")
    fun recreatedHostCompletesWaitingCoroutine() =
        runTest(dispatcher) {
            val original = startedHost()
            val status = async { original.runChallenge(data) }

            val recreated = original.recreate()
            recreated.challengeEnds(ChallengeActivityResult.TimedOut)

            assertEquals(ThreeDSSDKChallengeStatus.TIMEOUT, status.await())
            assertTrue(recreated.orphaned.isEmpty())
            verify(exactly = 1) { original.launcher.launch(extra) }
            verify(exactly = 0) { recreated.launcher.launch(any()) }
        }

    @Test
    @DisplayName("When the host is finished while its challenge shows, the runner returns a RuntimeError status")
    fun finishedHostEndsWaitingChallenge() =
        runTest(dispatcher) {
            val fake = startedHost()
            val status = async { fake.runChallenge(data) }

            fake.isFinishing = true
            fake.dispatch(Lifecycle.Event.ON_DESTROY)

            assertTrue(checkNotNull(status.await()).startsWith(ThreeDSSDKChallengeStatus.RUNTIME_ERROR))
        }

    @Test
    @DisplayName("When the host was finished, a challenge cannot start")
    fun finishedHostRejectsChallenge() =
        runTest(dispatcher) {
            val fake = startedHost()
            val runChallenge = fake.runChallenge
            fake.isFinishing = true
            fake.dispatch(Lifecycle.Event.ON_DESTROY)

            assertThrows<IllegalStateException> { runChallenge(data) }
        }

    @Test
    @DisplayName("A host is retired once its owner is finished, but not when it is recreated")
    fun hostIsRetiredOnlyWhenFinished() {
        val fake = startedHost()
        fake.dispatch(Lifecycle.Event.ON_DESTROY)
        assertFalse(fake.host.isRetired)

        fake.isFinishing = true
        fake.dispatch(Lifecycle.Event.ON_DESTROY)
        assertTrue(fake.host.isRetired)
    }

    @Test
    @DisplayName("A Fragment host is finished when its parent Fragment is removed")
    fun fragmentHostFinishedWithRemovedParent() {
        val parent: Fragment = mockk { every { isRemoving } returns true }
        val child = FakeFragment(parent)

        child.dispatch(Lifecycle.Event.ON_DESTROY)

        assertTrue(child.host.isRetired)
    }

    @Test
    @DisplayName("A Fragment host removed with its state saved keeps its session for the restored Fragment")
    fun fragmentRemovedWithSavedStateKeepsSession() {
        val fake = FakeFragment()
        fake.dispatch(Lifecycle.Event.ON_CREATE)
        fake.dispatch(Lifecycle.Event.ON_START)
        val sessionId = fake.host.resolveSessionId()
        assertTrue(ChallengeSessions.tryBegin(sessionId))

        // As FragmentManager.saveBackStack() (Navigation bottom navigation) removes a Fragment.
        fake.isRemoving = true
        fake.dispatch(Lifecycle.Event.ON_STOP)
        val saved = fake.saveState()
        fake.dispatch(Lifecycle.Event.ON_DESTROY)
        assertFalse(fake.host.isRetired)

        val result = JudoPaymentResult.Success(JudoResult())
        ChallengeSessions.deliver(sessionId, result)
        val restored = FakeHost(restoredState = saved).apply { dispatch(Lifecycle.Event.ON_CREATE) }

        assertEquals(sessionId, restored.sessionId)
        assertEquals(listOf(result), restored.results)
    }

    @Test
    @DisplayName("A Fragment host whose state was saved, then started again, is finished when removed")
    fun fragmentRemovedAfterRestartIsFinished() {
        val fake = FakeFragment()
        fake.dispatch(Lifecycle.Event.ON_CREATE)
        fake.dispatch(Lifecycle.Event.ON_START)
        fake.dispatch(Lifecycle.Event.ON_STOP)
        fake.saveState()
        fake.dispatch(Lifecycle.Event.ON_START)

        fake.isRemoving = true
        fake.dispatch(Lifecycle.Event.ON_STOP)
        fake.dispatch(Lifecycle.Event.ON_DESTROY)

        assertTrue(fake.host.isRetired)
    }

    @Test
    @DisplayName("Saved state from before a challenge ended doesn't restore it in the same process")
    fun staleSavedStateDoesNotRestoreChallenge() =
        runTest(dispatcher) {
            val original = startedHost()
            val status = async { original.runChallenge(data) }
            val saved = original.saveState()
            original.challengeEnds(ChallengeActivityResult.Cancelled)
            assertEquals(ThreeDSSDKChallengeStatus.CANCELLED, status.await())

            val recreated = FakeHost(restoredState = saved).apply { dispatch(Lifecycle.Event.ON_CREATE) }

            assertFalse(recreated.host.isTransactionInProgress())
            assertTrue(ChallengeSessions.tryBegin(recreated.sessionId))
        }

    @Test
    @DisplayName("A result delivered after the host was finished is dropped, and the session is forgotten")
    fun finishedSessionIsForgotten() {
        val fake = startedHost()
        val sessionId = fake.sessionId
        assertTrue(ChallengeSessions.tryBegin(sessionId))
        fake.isFinishing = true
        fake.dispatch(Lifecycle.Event.ON_DESTROY)

        ChallengeSessions.deliver(sessionId, JudoPaymentResult.Success(JudoResult()))

        assertTrue(fake.results.isEmpty())
        assertFalse(ChallengeSessions.contains(sessionId))
    }

    @Test
    @DisplayName("A second challenge cannot start while one is running for the session")
    fun secondChallengeIsRejected() =
        runTest(dispatcher) {
            val fake = startedHost()
            backgroundScope.async { fake.runChallenge(data) }

            assertThrows<IllegalStateException> { fake.runChallenge(data) }
        }

    @Test
    @DisplayName("Only one transaction at a time can begin per session")
    fun oneTransactionAtATime() {
        val fake = startedHost()

        assertTrue(ChallengeSessions.tryBegin(fake.sessionId))
        assertFalse(ChallengeSessions.tryBegin(fake.sessionId))

        ChallengeSessions.deliver(fake.sessionId, JudoPaymentResult.Success(JudoResult()))
        assertTrue(ChallengeSessions.tryBegin(fake.sessionId))
    }

    @Test
    @DisplayName("Saved state carries the session and the pending transaction while a challenge is showing")
    fun savedStateCarriesPendingChallenge() =
        runTest(dispatcher) {
            val fake = startedHost()
            backgroundScope.async { fake.runChallenge(data) }

            val saved = fake.saveState()

            assertEquals(fake.sessionId, saved.getString("sessionId"))
            assertEquals("receipt-1", saved.getString("receiptId"))
            assertEquals("2.2.0", saved.getString("messageVersion"))
        }

    @Test
    @DisplayName("After process death, the outcome is handed on with the restored pending transaction")
    fun resultAfterProcessDeathIsOrphaned() {
        val saved = fakeBundle("sessionId" to "session-1", "receiptId" to "receipt-1", "messageVersion" to "2.2.0")
        val fake = FakeHost(restoredState = saved)

        fake.challengeEnds(ChallengeActivityResult.Cancelled)

        assertEquals(listOf(PendingChallenge("receipt-1", "2.2.0") to ChallengeActivityResult.Cancelled), fake.orphaned)
    }

    @Test
    @DisplayName("An outcome with no transaction to complete delivers an error to the host")
    fun resultWithoutPendingDeliversError() {
        val fake = startedHost()

        fake.challengeEnds(ChallengeActivityResult.Cancelled)

        assertTrue(fake.orphaned.isEmpty())
        assertTrue(fake.results.single() is JudoPaymentResult.Error)
    }

    @Test
    @DisplayName("A result delivered while no host is created is held until one is")
    fun deliverBuffersUntilAttach() {
        val original = startedHost()
        val saved = original.saveState()
        original.dispatch(Lifecycle.Event.ON_DESTROY)

        val result = JudoPaymentResult.Success(JudoResult())
        ChallengeSessions.deliver(original.sessionId, result)
        assertTrue(original.results.isEmpty())

        val recreated = FakeHost(restoredState = saved)
        recreated.dispatch(Lifecycle.Event.ON_CREATE)

        assertEquals(listOf(result), recreated.results)
        assertNull(ChallengeSessions.pending(recreated.sessionId))
    }

    @Test
    @DisplayName("isTransactionInProgress covers a transaction from its start until its result is delivered")
    fun transactionInProgressUntilDelivered() {
        val fake = startedHost()
        assertFalse(fake.host.isTransactionInProgress())

        ChallengeSessions.tryBegin(fake.sessionId)
        assertTrue(fake.recreate().host.isTransactionInProgress())

        ChallengeSessions.deliver(fake.sessionId, JudoPaymentResult.Success(JudoResult()))
        assertFalse(fake.host.isTransactionInProgress())
    }

    @Test
    @DisplayName("isTransactionInProgress covers a challenge restored after process death")
    fun transactionInProgressAfterProcessDeath() {
        val saved = fakeBundle("sessionId" to "session-1", "receiptId" to "receipt-1", "messageVersion" to "2.2.0")
        val fake = FakeHost(restoredState = saved)
        fake.dispatch(Lifecycle.Event.ON_CREATE)

        assertTrue(fake.host.isTransactionInProgress())
    }
}
