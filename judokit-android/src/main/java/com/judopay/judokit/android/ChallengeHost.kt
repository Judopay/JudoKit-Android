package com.judopay.judokit.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import com.judopay.judo3ds2.ui.challenge.ChallengeActivityResult
import com.judopay.judo3ds2.ui.challenge.ChallengeActivityResultContract
import com.judopay.judo3ds2.ui.challenge.model.ChallengeExtra
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.service.ChallengeData
import com.judopay.judokit.android.service.ChallengeRunner
import com.judopay.judokit.android.service.toFormattedEventString
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private const val SAVED_STATE_KEY = "com.judopay.judokit.android.ChallengeHost"
private const val KEY_SESSION_ID = "sessionId"
private const val KEY_RECEIPT_ID = "receiptId"
private const val KEY_MESSAGE_VERSION = "messageVersion"
private val TAG = ChallengeHost::class.java.name

/** What `complete3ds2` needs to finish a transaction whose challenge is showing. Holds no card data. */
internal data class PendingChallenge(
    val receiptId: String,
    val messageVersion: String,
)

/**
 * Process-wide state of each [ChallengeHost] session, keyed by a session ID that the host persists
 * in its saved state. It outlives any single Activity/Fragment instance, so a host recreated in
 * the same process picks up where the destroyed one left off. Challenges are launched through
 * whichever host instance is current when they start, never through one captured earlier.
 *
 * Only accessed on the main thread.
 */
@Suppress("TooManyFunctions")
internal object ChallengeSessions {
    private class Session {
        /** The current host instance's launcher; `null` while no instance is created. */
        var launcher: ActivityResultLauncher<ChallengeExtra>? = null

        /** The current host instance's result callback; `null` while none is created. */
        var receiver: JudoCardTransactionCallback? = null

        /** The transaction coroutine suspended on the challenge outcome. */
        var waiter: CompletableDeferred<ChallengeActivityResult>? = null

        /** The transaction the challenge is showing for, persisted in the host's saved state. */
        var pending: PendingChallenge? = null

        /**
         * The current host instance is STARTED. A challenge is only launched then,
         * launching from a stopped (backgrounded) host can be blocked by the background
         * activity start restrictions, leaving the transaction waiting forever.
         */
        var started = false

        /** A challenge started while no host instance was STARTED; launched when one is. */
        var awaitingLaunch: ChallengeExtra? = null

        /** A client transaction is running, from its first API call until its result is delivered. */
        var inFlight = false

        /** A result delivered while no host instance was created, held until one is. */
        var undelivered: JudoPaymentResult? = null

        /** The host was finished for good (not recreated): no challenge outcome can reach it any more. */
        var finished = false

        val isIdle get() = !inFlight && waiter == null && pending == null && undelivered == null
    }

    private val sessions = ConcurrentHashMap<String, Session>()

    private fun session(sessionId: String) = sessions.getOrPut(sessionId, ::Session)

    /** Makes [launcher] and [receiver] current for the session, and hands [receiver] any held result. */
    @MainThread
    fun attach(
        sessionId: String,
        launcher: ActivityResultLauncher<ChallengeExtra>,
        receiver: JudoCardTransactionCallback?,
    ) {
        val session = session(sessionId)
        session.finished = false
        session.launcher = launcher
        session.receiver = receiver
        session.started = false
        val undelivered = session.undelivered
        if (receiver != null && undelivered != null) {
            session.undelivered = null
            receiver.onResult(undelivered)
        }
    }

    /** Records whether the host instance owning [launcher] is STARTED, launching a waiting challenge once it is. */
    @MainThread
    fun setStarted(
        sessionId: String,
        launcher: ActivityResultLauncher<ChallengeExtra>,
        started: Boolean,
    ) {
        val session = sessions[sessionId]?.takeIf { it.launcher === launcher } ?: return
        session.started = started
        session.awaitingLaunch?.takeIf { started }?.let { extra ->
            session.awaitingLaunch = null
            launch(session, launcher, extra)
        }
    }

    /** Called when a host instance is destroyed; it is recreated later unless [finished] is `true`. */
    @MainThread
    fun detach(
        sessionId: String,
        launcher: ActivityResultLauncher<ChallengeExtra>,
        finished: Boolean,
    ) {
        val session = sessions[sessionId] ?: return
        if (session.launcher === launcher) {
            session.launcher = null
            session.receiver = null
            session.started = false
        }
        if (!finished) return
        // Nothing can launch the challenge or receive its outcome any more: end the waiting
        // transaction now (it is still completed via complete3ds2) instead of suspending forever.
        session.finished = true
        session.awaitingLaunch = null
        session.waiter?.complete(
            ChallengeActivityResult.RuntimeError(null, "The 3DS2 challenge host was finished before the challenge ended."),
        )
        session.undelivered?.let { Log.w(TAG, "Dropping a ${it.javaClass.simpleName} result whose host was finished") }
        session.undelivered = null
        pruneIfDone(sessionId, session)
    }

    /**
     * Restores the [pending] challenge of a session recreated from saved state after process death.
     * A session this process already knows is ignored: its saved state may predate the end of its
     * challenge, and restoring it would leave a challenge pending that never ends.
     */
    @MainThread
    fun restore(
        sessionId: String,
        pending: PendingChallenge,
    ) {
        if (!sessions.containsKey(sessionId)) sessions[sessionId] = Session().also { it.pending = pending }
    }

    @MainThread
    fun pending(sessionId: String): PendingChallenge? = sessions[sessionId]?.pending

    /**
     * Marks a client transaction as started. Returns `false`, starting nothing, if one is already
     * in progress for this session: a session runs one transaction at a time.
     */
    @MainThread
    fun tryBegin(sessionId: String): Boolean {
        val session = session(sessionId)
        if (session.inFlight || session.pending != null) return false
        session.inFlight = true
        return true
    }

    @MainThread
    fun isTransactionInProgress(sessionId: String): Boolean = sessions[sessionId]?.let { it.inFlight || it.pending != null } == true

    /**
     * A [ChallengeRunner] for [sessionId]. It holds no reference to any host instance, so it can be
     * kept by a transaction coroutine or a ViewModel that outlives them. It holds the session
     * itself, so it still sees that the host was finished after the session is forgotten.
     */
    @MainThread
    fun runner(
        sessionId: String,
        mainDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    ): ChallengeRunner {
        val session = session(sessionId)
        return { data -> runChallenge(sessionId, session, data, mainDispatcher) }
    }

    /**
     * Launches [data]'s challenge through the session's current host instance if it is STARTED,
     * otherwise once a host instance of the session is STARTED (e.g. the app returns to the
     * foreground, or a recreated instance starts), and suspends until its outcome arrives. Returns the
     * `threeDSSDKChallengeStatus` string for `complete3ds2`.
     *
     * @throws IllegalStateException if the host was finished, or a challenge is already running.
     * Any exception means the challenge did not run; the caller still completes the transaction.
     */
    private suspend fun runChallenge(
        sessionId: String,
        session: Session,
        data: ChallengeData,
        mainDispatcher: CoroutineDispatcher,
    ): String =
        withContext(mainDispatcher) {
            check(!session.finished) { "The 3DS2 challenge host was finished before the challenge started." }
            check(session.waiter == null) { "A 3DS2 challenge is already running for this session." }
            val outcome = CompletableDeferred<ChallengeActivityResult>()
            session.waiter = outcome
            session.pending = PendingChallenge(data.receiptId, data.messageVersion)
            try {
                val extra = data.toChallengeExtra()
                val launcher = session.launcher
                if (launcher != null && session.started) launch(session, launcher, extra) else session.awaitingLaunch = extra
                outcome.await().toFormattedEventString()
            } finally {
                session.waiter = null
                session.pending = null
                session.awaitingLaunch = null
                pruneIfDone(sessionId, session)
            }
        }

    private fun launch(
        session: Session,
        launcher: ActivityResultLauncher<ChallengeExtra>,
        extra: ChallengeExtra,
    ) {
        try {
            launcher.launch(extra)
        } catch (
            @Suppress("TooGenericExceptionCaught") e: RuntimeException,
        ) {
            // E.g. SDKRuntimeException if the 3DS2 transaction is no longer valid.
            session.waiter?.complete(ChallengeActivityResult.RuntimeError(null, e.message ?: "Failed to launch the 3DS2 challenge."))
        }
    }

    /**
     * Routes a challenge outcome received by the session's host: to the waiting transaction
     * coroutine if there is one, otherwise (after process death) to [onOrphaned] with the restored
     * pending transaction. Returns `false` if there is no transaction to complete at all.
     */
    @MainThread
    fun onChallengeResult(
        sessionId: String,
        result: ChallengeActivityResult,
        onOrphaned: (PendingChallenge) -> Unit,
    ): Boolean {
        val session = sessions[sessionId]
        val waiter = session?.waiter
        val pending = session?.pending
        return when {
            waiter != null -> waiter.complete(result).let { true }
            pending != null -> {
                session.pending = null
                onOrphaned(pending)
                true
            }
            else -> false
        }
    }

    /** Delivers [result] to the session's current receiver, or holds it until one attaches. */
    @MainThread
    fun deliver(
        sessionId: String,
        result: JudoPaymentResult,
    ) {
        val session = session(sessionId)
        session.inFlight = false
        val receiver = session.receiver
        when {
            receiver != null -> receiver.onResult(result)
            session.finished -> Log.w(TAG, "Dropping a ${result.javaClass.simpleName} result whose host was finished")
            else -> session.undelivered = result
        }
        pruneIfDone(sessionId, session)
    }

    /** Ends a client transaction that was cancelled, so it no longer counts as in progress. */
    @MainThread
    fun abandon(sessionId: String) {
        val session = sessions[sessionId] ?: return
        session.inFlight = false
        pruneIfDone(sessionId, session)
    }

    private fun pruneIfDone(
        sessionId: String,
        session: Session,
    ) {
        if (session.finished && session.isIdle) sessions.remove(sessionId, session)
    }

    @VisibleForTesting
    fun contains(sessionId: String) = sessions.containsKey(sessionId)

    @VisibleForTesting
    fun reset() = sessions.clear()
}

/**
 * Hosts the 3DS2 challenges of one Activity/Fragment: registers the
 * [ChallengeActivityResultContract] launcher on [caller] when it is created, and keeps the
 * session that ties the instances of the same Activity/Fragment together.
 *
 * - Same-process recreation (rotation, "Don't keep activities"): the recreated host re-registers
 *   the launcher and reattaches to the same session, so the outcome reaches the transaction
 *   coroutine that is still waiting for it, a challenge started while no instance was STARTED is
 *   launched once the new one is, and the final result reaches the new host's [receiver].
 * - Process death: the waiting coroutine is gone. The host restores the [PendingChallenge] from its
 *   saved state and, when the challenge outcome arrives, hands it to [onOrphanedResult] so the
 *   transaction is still completed.
 * - Finished for good ([isFinishing] is `true` when it is destroyed): a transaction waiting on its
 *   challenge is completed as a runtime error rather than waiting forever.
 *
 * - Stopped (in the background): a challenge is not launched until the owner is STARTED again.
 *
 * Must be constructed before the owner is STARTED (e.g. as a field, or in `onCreate`), like any
 * `registerForActivityResult` call. Only one [ChallengeHost] per owner is supported, and a Fragment
 * instance can't be reused after it is removed (see [isRetired]).
 */
@Suppress("LongParameterList")
internal class ChallengeHost(
    caller: ActivityResultCaller,
    private val owner: SavedStateRegistryOwner,
    private val isFinishing: (stateSaved: Boolean) -> Boolean,
    val receiver: JudoCardTransactionCallback?,
    private val onOrphanedResult: (sessionId: String, PendingChallenge, ChallengeActivityResult) -> Unit,
    private val newBundle: () -> Bundle = ::Bundle,
) {
    private var sessionId: String? = null

    /**
     * The owner's state was saved since it was last started, so it can be restored from that state
     * after it is destroyed (a configuration change, a Fragment back stack saved by Navigation, a
     * ViewPager2 page scrolled away).
     */
    private var stateSaved = false

    /**
     * The owner was destroyed for good, so this host can't run challenges any more. A removed
     * Fragment instance that is added again stays retired: AndroidX gives it a new lifecycle and
     * saved-state registry, and its launcher stays bound to the destroyed lifecycle.
     */
    var isRetired = false
        private set

    private val launcher: ActivityResultLauncher<ChallengeExtra> =
        caller.registerForActivityResult(ChallengeActivityResultContract(), ::onChallengeResult)

    init {
        owner.savedStateRegistry.registerSavedStateProvider(SAVED_STATE_KEY) {
            val id = resolveSessionId()
            val pending = ChallengeSessions.pending(id)
            stateSaved = true
            newBundle().apply {
                putString(KEY_SESSION_ID, id)
                putString(KEY_RECEIPT_ID, pending?.receiptId)
                putString(KEY_MESSAGE_VERSION, pending?.messageVersion)
            }
        }
        owner.lifecycle.addObserver(
            object : LifecycleEventObserver {
                override fun onStateChanged(
                    source: LifecycleOwner,
                    event: Lifecycle.Event,
                ) {
                    when (event) {
                        Lifecycle.Event.ON_CREATE -> ChallengeSessions.attach(resolveSessionId(), launcher, receiver)
                        Lifecycle.Event.ON_START -> {
                            stateSaved = false
                            ChallengeSessions.setStarted(resolveSessionId(), launcher, true)
                        }
                        Lifecycle.Event.ON_STOP -> ChallengeSessions.setStarted(resolveSessionId(), launcher, false)
                        Lifecycle.Event.ON_DESTROY -> {
                            val finished = isFinishing(stateSaved)
                            if (finished) isRetired = true
                            ChallengeSessions.detach(resolveSessionId(), launcher, finished)
                        }
                        else -> Unit
                    }
                }
            },
        )
    }

    /** This host's session, restored from saved state when the owner is recreated. */
    @MainThread
    fun resolveSessionId(): String {
        sessionId?.let { return it }
        val registry = owner.savedStateRegistry
        val restored = if (registry.isRestored) registry.consumeRestoredStateForKey(SAVED_STATE_KEY) else null
        val id = restored?.getString(KEY_SESSION_ID) ?: UUID.randomUUID().toString()
        restored?.toPendingChallenge()?.let { ChallengeSessions.restore(id, it) }
        sessionId = id
        return id
    }

    /**
     * Whether a transaction of this session is in progress: from its first API call until its result
     * is delivered, including one started before the owner was recreated, and a challenge restored
     * after process death.
     */
    @MainThread
    fun isTransactionInProgress(): Boolean = ChallengeSessions.isTransactionInProgress(resolveSessionId())

    private fun onChallengeResult(result: ChallengeActivityResult) {
        val id = resolveSessionId()
        val handled = ChallengeSessions.onChallengeResult(id, result) { pending -> onOrphanedResult(id, pending, result) }
        if (handled) return
        Log.w(TAG, "3DS2 challenge result (${result.javaClass.simpleName}) arrived with no transaction to complete")
        if (receiver != null) {
            ChallengeSessions.deliver(
                id,
                JudoPaymentResult.Error(JudoError.judoInternalError("3DS2 challenge ended with no transaction to complete.")),
            )
        }
    }

    private fun Bundle.toPendingChallenge(): PendingChallenge? {
        val receiptId = getString(KEY_RECEIPT_ID)
        val messageVersion = getString(KEY_MESSAGE_VERSION)
        return if (receiptId != null && messageVersion != null) PendingChallenge(receiptId, messageVersion) else null
    }

    companion object {
        /** A host for [activity]; it is finished for good once [activity] is finishing. */
        fun of(
            activity: ComponentActivity,
            receiver: JudoCardTransactionCallback?,
            onOrphanedResult: (sessionId: String, PendingChallenge, ChallengeActivityResult) -> Unit,
        ) = ChallengeHost(activity, activity, { activity.isFinishing }, receiver, onOrphanedResult)

        /**
         * A host for [fragment]; it is finished for good once its Activity is finishing, or
         * [fragment] or one of its parent Fragments is removed without its state being saved. A
         * Fragment removed with its state saved (a back stack saved by Navigation's bottom
         * navigation, a ViewPager2 FragmentStateAdapter page) is restored from that state later,
         * like a recreated one. A child Fragment destroyed with its removed parent isn't itself
         * marked as removing.
         */
        fun of(
            fragment: Fragment,
            receiver: JudoCardTransactionCallback?,
            newBundle: () -> Bundle = ::Bundle,
            onOrphanedResult: (sessionId: String, PendingChallenge, ChallengeActivityResult) -> Unit,
        ) = ChallengeHost(
            fragment,
            fragment,
            { stateSaved ->
                fragment.activity?.isFinishing != false ||
                    (!stateSaved && generateSequence(fragment) { it.parentFragment }.any { it.isRemoving })
            },
            receiver,
            onOrphanedResult,
            newBundle,
        )
    }
}
