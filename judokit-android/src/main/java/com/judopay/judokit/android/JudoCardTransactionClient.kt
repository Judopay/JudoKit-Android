// The Context-based API still runs challenges through the deprecated Transaction.doChallenge().
@file:Suppress("DEPRECATION")

package com.judopay.judokit.android

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.judopay.judo3ds2.model.CompletionEvent
import com.judopay.judo3ds2.model.ProtocolErrorEvent
import com.judopay.judo3ds2.model.RuntimeErrorEvent
import com.judopay.judo3ds2.transaction.challenge.ChallengeStatusReceiver
import com.judopay.judo3ds2.ui.challenge.ChallengeActivityResult
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.service.ChallengeData
import com.judopay.judokit.android.service.ChallengeRunner
import com.judopay.judokit.android.service.THREE_DS_TWO_MIN_TIMEOUT
import com.judopay.judokit.android.service.ThreeDSSDKChallengeStatus
import com.judopay.judokit.android.service.toFormattedEventString
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

private val TAG = JudoCardTransactionClient::class.java.name

/**
 * Handles the 3DS2 native challenge through the deprecated `Transaction.doChallenge()` and returns
 * the raw challenge status string. Only used by the deprecated `Context`-based client methods.
 *
 * Bridges [ChallengeStatusReceiver] callbacks into a coroutine continuation. Safe to cancel:
 * if the coroutine is cancelled the continuation is never resumed.
 */
private suspend fun runLegacyChallenge(
    context: Context,
    data: ChallengeData,
): String? =
    suspendCancellableCoroutine { cont ->
        data.transaction.doChallenge(
            context.applicationContext,
            data.challengeParameters,
            object : ChallengeStatusReceiver {
                override fun completed(event: CompletionEvent) = cont.resumeWith(Result.success(event.toFormattedEventString()))

                override fun cancelled() = cont.resumeWith(Result.success(ThreeDSSDKChallengeStatus.CANCELLED))

                override fun protocolError(event: ProtocolErrorEvent) = cont.resumeWith(Result.success(event.toFormattedEventString()))

                override fun runtimeError(event: RuntimeErrorEvent) = cont.resumeWith(Result.success(event.toFormattedEventString()))

                override fun timedout() = cont.resumeWith(Result.success(ThreeDSSDKChallengeStatus.TIMEOUT))
            },
            THREE_DS_TWO_MIN_TIMEOUT,
        )
    }

/**
 * Receives the outcome of a card transaction initiated via [JudoCardTransactionClient].
 *
 * Declared as a SAM-compatible functional interface so it can be used as a lambda from both
 * Kotlin and Java.
 *
 * Java example:
 * ```java
 * client.payment(context, details, result -> handleResult(result));
 * ```
 *
 * Kotlin example:
 * ```kotlin
 * client.payment(context, details) { result -> handleResult(result) }
 * ```
 */
fun interface JudoCardTransactionCallback {
    /**
     * Called on the main thread when the transaction has completed.
     *
     * @param result [JudoPaymentResult.Success], [JudoPaymentResult.Error], or
     *               [JudoPaymentResult.UserCancelled].
     */
    @MainThread
    fun onResult(result: JudoPaymentResult)
}

/**
 * Entry point for performing card transactions without the Judo-provided UI screens.
 *
 * Create one instance per Activity or Fragment with [JudoCardTransactionClient.create], passing the
 * Activity/Fragment itself. Do this unconditionally while it is being created (as a field
 * initializer, or in `onCreate`), like any `registerForActivityResult` call, then call whichever
 * transaction method matches your use-case. Results are delivered to the
 * [JudoCardTransactionCallback] passed to [create]. A client runs one transaction at a time: a
 * transaction method called while another transaction is in progress is ignored.
 *
 * When the issuing bank requires a 3DS2 challenge, the SDK launches the challenge screen through
 * [com.judopay.judo3ds2.ui.challenge.ChallengeActivityResultContract] and completes the transaction
 * automatically. Callers do not need to handle the 3DS2 flow themselves.
 *
 * The transaction survives the Activity/Fragment being recreated, including after the app process
 * is killed while the challenge screen is showing (e.g. while the cardholder is in their banking
 * app): the recreated Activity/Fragment creates its client again, and the result is delivered to
 * that new instance's callback. The callback is always invoked on the **main thread**, while the
 * Activity/Fragment is at least created.
 *
 * Java example:
 * ```java
 * // A field of your Activity/Fragment:
 * private final JudoCardTransactionClient client =
 *     JudoCardTransactionClient.create(this, judo, result -> {
 *         if (result instanceof JudoPaymentResult.Success) { ... }
 *     });
 *
 * client.payment(details);
 * ```
 *
 * Kotlin example:
 * ```kotlin
 * private val client =
 *     JudoCardTransactionClient.create(this, judo) { result ->
 *         when (result) {
 *             is JudoPaymentResult.Success      -> handleSuccess(result.result)
 *             is JudoPaymentResult.Error        -> handleError(result.error)
 *             is JudoPaymentResult.UserCancelled -> handleCancelled()
 *         }
 *     }
 *
 * client.payment(details)
 * ```
 */
@Suppress("TooManyFunctions")
class JudoCardTransactionClient internal constructor(
    repositoryProvider: () -> CardTransactionRepository,
    private val coroutineScope: CoroutineScope,
    private val callbackDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    private val challengeHostProvider: ((JudoCardTransactionClient) -> ChallengeHost)? = null,
) {
    internal constructor(
        repository: CardTransactionRepository,
        coroutineScope: CoroutineScope,
        callbackDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    ) : this({ repository }, coroutineScope, callbackDispatcher)

    // Lazy: an Activity/Fragment has no usable Context yet while its fields are initialized.
    private val repository by lazy(repositoryProvider)

    // Created eagerly, so the challenge launcher is registered while the host is being created.
    private val challengeHost: ChallengeHost? = challengeHostProvider?.invoke(this)

    /**
     * Whether a transaction started on this client (or on the one a recreated Activity/Fragment
     * had before) is in progress: from its first API call until its result is delivered, including
     * a 3DS2 challenge restored after process death. Only valid once the Activity/Fragment is
     * created.
     */
    internal val isTransactionInProgress: Boolean
        @MainThread get() = challengeHost?.isTransactionInProgress() == true

    /**
     * Performs a card payment. The result is delivered to the callback passed to [create].
     *
     * @param details Card and billing details for the transaction.
     * @throws IllegalStateException if this client was created with the deprecated
     * `create(Context, Judo)`.
     */
    @MainThread
    fun payment(details: TransactionDetails) = executeHosted { payment(details, it) }

    /**
     * Performs a card pre-authorisation. The result is delivered to the callback passed to [create].
     *
     * @param details Card and billing details for the transaction.
     * @throws IllegalStateException if this client was created with the deprecated
     * `create(Context, Judo)`.
     */
    @MainThread
    fun preAuth(details: TransactionDetails) = executeHosted { preAuth(details, it) }

    /**
     * Performs a token payment using a previously saved card. The result is delivered to the
     * callback passed to [create].
     *
     * @param details Token and billing details for the transaction.
     * @throws IllegalStateException if this client was created with the deprecated
     * `create(Context, Judo)`.
     */
    @MainThread
    fun paymentWithToken(details: TransactionDetails) = executeHosted { paymentWithToken(details, it) }

    /**
     * Performs a token pre-authorisation using a previously saved card. The result is delivered to
     * the callback passed to [create].
     *
     * @param details Token and billing details for the transaction.
     * @throws IllegalStateException if this client was created with the deprecated
     * `create(Context, Judo)`.
     */
    @MainThread
    fun preAuthWithToken(details: TransactionDetails) = executeHosted { preAuthWithToken(details, it) }

    /**
     * Saves a card without performing a payment (register card flow). The result is delivered to
     * the callback passed to [create].
     *
     * @param details Card and billing details.
     * @throws IllegalStateException if this client was created with the deprecated
     * `create(Context, Judo)`.
     */
    @MainThread
    fun save(details: TransactionDetails) = executeHosted { save(details, it) }

    /**
     * Checks a card without performing a payment. The result is delivered to the callback passed
     * to [create].
     *
     * @param details Card and billing details.
     * @throws IllegalStateException if this client was created with the deprecated
     * `create(Context, Judo)`.
     */
    @MainThread
    fun check(details: TransactionDetails) = executeHosted { check(details, it) }

    /**
     * Performs a card payment.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Card and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    @Deprecated(DEPRECATED_CONTEXT_API, ReplaceWith("payment(details)"))
    fun payment(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.payment(details) { runLegacyChallenge(context, it) }
    }

    /**
     * Performs a card pre-authorisation.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Card and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    @Deprecated(DEPRECATED_CONTEXT_API, ReplaceWith("preAuth(details)"))
    fun preAuth(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.preAuth(details) { runLegacyChallenge(context, it) }
    }

    /**
     * Performs a token payment using a previously saved card.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Token and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    @Deprecated(DEPRECATED_CONTEXT_API, ReplaceWith("paymentWithToken(details)"))
    fun paymentWithToken(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.paymentWithToken(details) { runLegacyChallenge(context, it) }
    }

    /**
     * Performs a token pre-authorisation using a previously saved card.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Token and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    @Deprecated(DEPRECATED_CONTEXT_API, ReplaceWith("preAuthWithToken(details)"))
    fun preAuthWithToken(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.preAuthWithToken(details) { runLegacyChallenge(context, it) }
    }

    /**
     * Saves a card without performing a payment (register card flow).
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Card and billing details.
     * @param callback Invoked on the main thread with the transaction result.
     */
    @Deprecated(DEPRECATED_CONTEXT_API, ReplaceWith("save(details)"))
    fun save(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.save(details) { runLegacyChallenge(context, it) }
    }

    /**
     * Checks a card without performing a payment.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Card and billing details.
     * @param callback Invoked on the main thread with the transaction result.
     */
    @Deprecated(DEPRECATED_CONTEXT_API, ReplaceWith("check(details)"))
    fun check(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.check(details) { runLegacyChallenge(context, it) }
    }

    private fun executeHosted(block: suspend CardTransactionRepository.(ChallengeRunner) -> JudoPaymentResult) {
        val host =
            checkNotNull(challengeHost) {
                "This client was created with create(Context, Judo). Use create(activity, judo, callback) " +
                    "or create(fragment, judo, callback) to call this method."
            }
        val sessionId = host.resolveSessionId()
        when {
            host.isRetired -> rejectRetired(host)
            !ChallengeSessions.tryBegin(sessionId) -> Log.w(TAG, "Ignored: a transaction of this client is already in progress.")
            else -> {
                // The runner is bound to the session, not to this host instance: if the Activity/Fragment is
                // recreated before the API responds, the challenge is launched through the recreated one.
                val runChallenge = ChallengeSessions.runner(sessionId, callbackDispatcher)
                launchTransaction(sessionId) { block(runChallenge) }
            }
        }
    }

    /** Its session is gone and nothing can launch a challenge: fails now rather than waiting forever. */
    private fun rejectRetired(host: ChallengeHost) {
        Log.w(TAG, "Rejected: the Activity/Fragment of this client was destroyed for good.")
        val receiver = host.receiver ?: return
        coroutineScope.launch(callbackDispatcher) {
            receiver.onResult(
                JudoPaymentResult.Error(
                    JudoError.judoInternalError("The Activity/Fragment of this client was destroyed. Create a new client."),
                ),
            )
        }
    }

    /**
     * Completes a transaction whose challenge outcome arrived after process death, when the
     * coroutine that launched the challenge no longer exists.
     */
    internal fun completeOrphanedChallenge(
        sessionId: String,
        pending: PendingChallenge,
        result: ChallengeActivityResult,
    ) {
        // The orphaned transaction is in progress until its result is delivered.
        ChallengeSessions.tryBegin(sessionId)
        val status = result.toFormattedEventString()
        launchTransaction(sessionId) { complete3ds2(pending.receiptId, pending.messageVersion, null, status) }
    }

    /**
     * Runs a transaction of [sessionId] in the process-wide [coroutineScope] and delivers its result
     * through the session. A failure is delivered as an error result, so the transaction never stays
     * in progress.
     *
     * The repository is resolved here and only it is handed to the coroutine: the coroutine can
     * outlive the host Activity/Fragment, so it must not capture this client, which references it.
     */
    @Suppress("TooGenericExceptionCaught")
    private fun launchTransaction(
        sessionId: String,
        block: suspend CardTransactionRepository.() -> JudoPaymentResult,
    ) {
        val repository =
            try {
                repository
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create the card transaction repository.", e)
                ChallengeSessions.deliver(sessionId, transactionFailed(e))
                return
            }
        coroutineScope.launch(callbackDispatcher) {
            val result =
                try {
                    repository.block()
                } catch (e: CancellationException) {
                    ChallengeSessions.abandon(sessionId)
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "The card transaction failed.", e)
                    transactionFailed(e)
                }
            ChallengeSessions.deliver(sessionId, result)
        }
    }

    private fun execute(
        callback: JudoCardTransactionCallback,
        block: suspend () -> JudoPaymentResult,
    ) {
        coroutineScope.launch(callbackDispatcher) { callback.onResult(block()) }
    }

    companion object {
        private fun transactionFailed(e: Exception) =
            JudoPaymentResult.Error(JudoError.judoInternalError(e.message ?: "The card transaction failed."))

        private const val DEPRECATED_CONTEXT_API =
            "The 3DS2 challenge outcome is lost if the app process dies during the challenge. " +
                "Create the client with create(activity, judo, callback) or create(fragment, judo, callback) " +
                "and use the methods without a Context parameter."

        /**
         * Creates a [JudoCardTransactionClient] bound to [activity], delivering every transaction
         * result to [callback] on the main thread.
         *
         * Must be called unconditionally while [activity] is being created, before it is STARTED
         * (e.g. as a field initializer or in `onCreate`), since it registers an Activity result
         * launcher. Create at most one client per Activity.
         *
         * @param activity The Activity that hosts the 3DS2 challenge screen.
         * @param judo     The Judo configuration for this payment session.
         * @param callback Receives the result of every transaction started on this client,
         *                 including a transaction that was started by a previous instance of
         *                 [activity] before it was recreated.
         */
        @JvmStatic
        fun create(
            activity: ComponentActivity,
            judo: Judo,
            callback: JudoCardTransactionCallback,
        ): JudoCardTransactionClient =
            createHosted({ activity.applicationContext }, judo, callback) { receiver, onOrphaned ->
                ChallengeHost.of(activity, receiver, onOrphaned)
            }

        /**
         * Creates a [JudoCardTransactionClient] bound to [fragment], delivering every transaction
         * result to [callback] on the main thread.
         *
         * Must be called unconditionally while [fragment] is being created (e.g. as a field
         * initializer or in `onCreate`), since it registers an Activity result launcher. Create at
         * most one client per Fragment.
         *
         * @param fragment The Fragment that hosts the 3DS2 challenge screen.
         * @param judo     The Judo configuration for this payment session.
         * @param callback Receives the result of every transaction started on this client,
         *                 including a transaction that was started by a previous instance of
         *                 [fragment] before it was recreated.
         */
        @JvmStatic
        fun create(
            fragment: Fragment,
            judo: Judo,
            callback: JudoCardTransactionCallback,
        ): JudoCardTransactionClient =
            createHosted({ fragment.requireContext() }, judo, callback) { receiver, onOrphaned ->
                ChallengeHost.of(fragment, receiver, onOrphanedResult = onOrphaned)
            }

        private fun createHosted(
            context: () -> Context,
            judo: Judo,
            callback: JudoCardTransactionCallback,
            hostFactory: (
                JudoCardTransactionCallback,
                (String, PendingChallenge, ChallengeActivityResult) -> Unit,
            ) -> ChallengeHost,
        ): JudoCardTransactionClient =
            JudoCardTransactionClient(
                repositoryProvider = { CardTransactionRepository.create(context(), judo) },
                coroutineScope = ProcessLifecycleOwner.get().lifecycleScope,
                challengeHostProvider = { client -> hostFactory(callback, client::completeOrphanedChallenge) },
            )

        /**
         * Creates a [JudoCardTransactionClient] configured for the given [judo] session.
         *
         * The application context is extracted from [context] internally, so passing an
         * [android.app.Activity] context is safe.
         *
         * @param context Any Android [Context].
         * @param judo    The Judo configuration for this payment session.
         */
        @Deprecated(
            DEPRECATED_CONTEXT_API,
            ReplaceWith("JudoCardTransactionClient.create(activity, judo, callback)"),
        )
        @JvmStatic
        fun create(
            context: Context,
            judo: Judo,
        ): JudoCardTransactionClient =
            JudoCardTransactionClient(
                CardTransactionRepository.create(context, judo),
                ProcessLifecycleOwner.get().lifecycleScope,
            )
    }
}

// ---------------------------------------------------------------------------
// Kotlin suspend extensions — coroutine-friendly wrappers over the callback API
// ---------------------------------------------------------------------------

/**
 * Bridges a [JudoCardTransactionCallback]-based call into a suspend function by wiring
 * the callback's [JudoCardTransactionCallback.onResult] directly into the coroutine continuation.
 */
private suspend fun awaitResult(block: (JudoCardTransactionCallback) -> Unit): JudoPaymentResult =
    suspendCancellableCoroutine { cont ->
        block { cont.resumeWith(Result.success(it)) }
    }

/**
 * Suspend variant of [JudoCardTransactionClient.payment] for Kotlin coroutine callers.
 */
@Deprecated(
    "The 3DS2 challenge outcome is lost if the app process dies during the challenge. " +
        "Create the client with create(activity, judo, callback) and call payment(details).",
)
suspend fun JudoCardTransactionClient.payment(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { payment(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.preAuth] for Kotlin coroutine callers.
 */
@Deprecated(
    "The 3DS2 challenge outcome is lost if the app process dies during the challenge. " +
        "Create the client with create(activity, judo, callback) and call preAuth(details).",
)
suspend fun JudoCardTransactionClient.preAuth(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { preAuth(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.paymentWithToken] for Kotlin coroutine callers.
 */
@Deprecated(
    "The 3DS2 challenge outcome is lost if the app process dies during the challenge. " +
        "Create the client with create(activity, judo, callback) and call paymentWithToken(details).",
)
suspend fun JudoCardTransactionClient.paymentWithToken(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { paymentWithToken(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.preAuthWithToken] for Kotlin coroutine callers.
 */
@Deprecated(
    "The 3DS2 challenge outcome is lost if the app process dies during the challenge. " +
        "Create the client with create(activity, judo, callback) and call preAuthWithToken(details).",
)
suspend fun JudoCardTransactionClient.preAuthWithToken(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { preAuthWithToken(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.save] for Kotlin coroutine callers.
 */
@Deprecated(
    "The 3DS2 challenge outcome is lost if the app process dies during the challenge. " +
        "Create the client with create(activity, judo, callback) and call save(details).",
)
suspend fun JudoCardTransactionClient.save(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { save(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.check] for Kotlin coroutine callers.
 */
@Deprecated(
    "The 3DS2 challenge outcome is lost if the app process dies during the challenge. " +
        "Create the client with create(activity, judo, callback) and call check(details).",
)
suspend fun JudoCardTransactionClient.check(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { check(context, details, it) }
