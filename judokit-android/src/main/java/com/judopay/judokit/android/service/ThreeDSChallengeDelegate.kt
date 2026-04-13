package com.judopay.judokit.android.service

import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val TAG = ThreeDSChallengeDelegate::class.java.name

/**
 * Manages the bridge between the 3DS2 SDK challenge callbacks and the ViewModel's coroutine
 * suspending on the result.
 *
 * Encapsulates the [pendingChallenge] state (used by the Fragment to start `doChallenge`) and the
 * internal channel that unblocks the suspended [challengeRunner] once the result arrives via
 * [onChallengeResult]. Extracted to eliminate identical boilerplate in [CardEntryViewModel] and
 * [PaymentMethodsViewModel].
 */
internal class ThreeDSChallengeDelegate {
    private val mutablePendingChallenge = MutableStateFlow<ChallengeData?>(null)

    /**
     * Emits the active [ChallengeData] while a native challenge is in progress, `null` otherwise.
     *
     * Exposed as [StateFlow] so that a Fragment recreated during a configuration change
     * immediately receives the pending challenge and can re-invoke `doChallenge` with the fresh
     * [androidx.fragment.app.FragmentActivity].
     */
    val pendingChallenge: StateFlow<ChallengeData?> = mutablePendingChallenge.asStateFlow()

    private val challengeResultChannel = Channel<String?>(1)

    /**
     * [ChallengeRunner] that stores the challenge data for the Fragment then suspends until
     * [onChallengeResult] delivers the status string.
     */
    val challengeRunner: ChallengeRunner = { transaction, params ->
        mutablePendingChallenge.value = ChallengeData(transaction, params)
        challengeResultChannel.receive()
    }

    /**
     * Called by the Fragment after the native 3DS2 challenge completes.
     *
     * Clears the pending challenge state so subsequent config-change re-subscriptions do not
     * re-trigger `doChallenge`. The first call wins; any duplicate call from a stale receiver is
     * silently dropped because the channel already holds a value.
     */
    fun onChallengeResult(status: String?) {
        mutablePendingChallenge.value = null
        val sent = challengeResultChannel.trySend(status)
        if (sent.isFailure) {
            Log.w(TAG, "Duplicate challenge result dropped: $status")
        }
    }
}
