package com.judopay.judokit.android

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.judopay.judo3ds2.model.CompletionEvent
import com.judopay.judo3ds2.model.ProtocolErrorEvent
import com.judopay.judo3ds2.model.RuntimeErrorEvent
import com.judopay.judo3ds2.transaction.Transaction
import com.judopay.judo3ds2.transaction.challenge.ChallengeParameters
import com.judopay.judo3ds2.transaction.challenge.ChallengeStatusReceiver
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.service.CardTransactionRepository
import com.judopay.judokit.android.service.THREE_DS_TWO_MIN_TIMEOUT
import com.judopay.judokit.android.service.ThreeDSSDKChallengeStatus
import com.judopay.judokit.android.service.toFormattedEventString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Handles the 3DS2 native challenge and returns the raw challenge status string.
 *
 * Bridges [ChallengeStatusReceiver] callbacks into a coroutine continuation. Safe to cancel:
 * if the coroutine is cancelled the continuation is never resumed.
 */
private suspend fun runChallenge(
    context: Context,
    transaction: Transaction,
    params: ChallengeParameters,
): String? =
    suspendCancellableCoroutine { cont ->
        transaction.doChallenge(
            context.applicationContext,
            params,
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
 * Create a single instance per payment session via [JudoCardTransactionClient.create], then call
 * whichever transaction method matches your use-case. All methods are callback-based and work
 * identically from Java and Kotlin.
 *
 * When the issuing bank requires a 3DS2 challenge, the SDK presents the challenge screen using the
 * supplied [Context] and resumes automatically. Callers do not need to handle the 3DS2 flow
 * themselves.
 *
 * The callback is always invoked on the **main thread**. The operation is tied to the
 * application's process lifecycle ([ProcessLifecycleOwner]) and will complete even if the
 * originating component is destroyed mid-transaction (e.g. rotation).
 *
 * Java example:
 * ```java
 * JudoCardTransactionClient client = JudoCardTransactionClient.create(context, judo);
 *
 * client.payment(context, details, result -> {
 *     if (result instanceof JudoPaymentResult.Success) { ... }
 * });
 * ```
 *
 * Kotlin example (callback API):
 * ```kotlin
 * val client = JudoCardTransactionClient.create(context, judo)
 *
 * client.payment(context, details) { result ->
 *     when (result) {
 *         is JudoPaymentResult.Success      -> handleSuccess(result.result)
 *         is JudoPaymentResult.Error        -> handleError(result.error)
 *         is JudoPaymentResult.UserCancelled -> handleCancelled()
 *     }
 * }
 * ```
 *
 * Kotlin example (suspend API — see extension functions below):
 * ```kotlin
 * val result = client.payment(context, details)
 * ```
 */
class JudoCardTransactionClient internal constructor(
    private val repository: CardTransactionRepository,
    private val coroutineScope: CoroutineScope,
    private val callbackDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) {
    /**
     * Performs a card payment.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Card and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun payment(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.payment(details) { t, p -> runChallenge(context, t, p) }
    }

    /**
     * Performs a card pre-authorisation.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Card and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun preAuth(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.preAuth(details) { t, p -> runChallenge(context, t, p) }
    }

    /**
     * Performs a token payment using a previously saved card.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Token and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun paymentWithToken(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.paymentWithToken(details) { t, p -> runChallenge(context, t, p) }
    }

    /**
     * Performs a token pre-authorisation using a previously saved card.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Token and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun preAuthWithToken(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.preAuthWithToken(details) { t, p -> runChallenge(context, t, p) }
    }

    /**
     * Saves a card without performing a payment (register card flow).
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Card and billing details.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun save(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.save(details) { t, p -> runChallenge(context, t, p) }
    }

    /**
     * Checks a card without performing a payment.
     *
     * @param context  Any Android [Context]; the application context is used internally.
     * @param details  Card and billing details.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun check(
        context: Context,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(callback) {
        repository.check(details) { t, p -> runChallenge(context, t, p) }
    }

    private fun execute(
        callback: JudoCardTransactionCallback,
        block: suspend () -> JudoPaymentResult,
    ) {
        coroutineScope.launch(callbackDispatcher) { callback.onResult(block()) }
    }

    companion object {
        /**
         * Creates a [JudoCardTransactionClient] configured for the given [judo] session.
         *
         * The application context is extracted from [context] internally, so passing an
         * [android.app.Activity] context is safe.
         *
         * @param context Any Android [Context].
         * @param judo    The Judo configuration for this payment session.
         */
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
suspend fun JudoCardTransactionClient.payment(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { payment(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.preAuth] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.preAuth(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { preAuth(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.paymentWithToken] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.paymentWithToken(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { paymentWithToken(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.preAuthWithToken] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.preAuthWithToken(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { preAuthWithToken(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.save] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.save(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { save(context, details, it) }

/**
 * Suspend variant of [JudoCardTransactionClient.check] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.check(
    context: Context,
    details: TransactionDetails,
): JudoPaymentResult = awaitResult { check(context, details, it) }
