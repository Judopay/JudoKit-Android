package com.judopay.judokit.android

import android.content.Context
import androidx.fragment.app.FragmentActivity
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Handles the 3DS2 native challenge for activity and returns the raw challenge status string.
 *
 * Bridges [ChallengeStatusReceiver] callbacks into a coroutine continuation. Safe to cancel:
 * if the coroutine is cancelled the continuation is never resumed.
 */
private suspend fun FragmentActivity.runChallenge(
    transaction: Transaction,
    params: ChallengeParameters,
): String? =
    suspendCancellableCoroutine { cont ->
        transaction.doChallenge(
            this,
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
 * client.payment(activity, details, result -> handleResult(result));
 * ```
 *
 * Kotlin example:
 * ```kotlin
 * client.payment(activity, details) { result -> handleResult(result) }
 * ```
 */
fun interface JudoCardTransactionCallback {
    /**
     * Called on the main thread when the transaction has completed.
     *
     * @param result [JudoPaymentResult.Success], [JudoPaymentResult.Error], or
     *               [JudoPaymentResult.UserCancelled].
     */
    fun onResult(result: JudoPaymentResult)
}

/**
 * Entry point for performing card transactions without the Judo-provided UI screens.
 *
 * Create a single instance per payment session via [JudoCardTransactionClient.create], then call
 * whichever transaction method matches your use-case. All methods are callback-based and work
 * identically from Java and Kotlin.
 *
 * When the issuing bank requires a 3DS2 challenge, the SDK presents the challenge screen on top
 * of the supplied [FragmentActivity] and resumes automatically. Callers do not need to handle
 * the 3DS2 flow themselves.
 *
 * callback is always invoked on the **main thread**. The operation is tied to the
 * [FragmentActivity]'s lifecycle: if the activity is destroyed before the transaction completes
 * the callback is not invoked.
 *
 * Java example:
 * ```java
 * JudoCardTransactionClient client = JudoCardTransactionClient.create(context, judo);
 *
 * client.payment(activity, details, result -> {
 *     if (result instanceof JudoPaymentResult.Success) { ... }
 * });
 * ```
 *
 * Kotlin example (callback API):
 * ```kotlin
 * val client = JudoCardTransactionClient.create(context, judo)
 *
 * client.payment(activity, details) { result ->
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
 * val result = client.payment(activity, details)
 * ```
 */
class JudoCardTransactionClient private constructor(
    private val repository: CardTransactionRepository,
) {
    /**
     * Performs a card payment.
     *
     * @param activity The [FragmentActivity] used to present the 3DS2 challenge if required.
     * @param details  Card and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun payment(
        activity: FragmentActivity,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(activity, callback) {
        repository.payment(details) { t, p -> activity.runChallenge(t, p) }
    }

    /**
     * Performs a card pre-authorisation.
     *
     * @param activity The [FragmentActivity] used to present the 3DS2 challenge if required.
     * @param details  Card and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun preAuth(
        activity: FragmentActivity,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(activity, callback) {
        repository.preAuth(details) { t, p -> activity.runChallenge(t, p) }
    }

    /**
     * Performs a token payment using a previously saved card.
     *
     * @param activity The [FragmentActivity] used to present the 3DS2 challenge if required.
     * @param details  Token and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun paymentWithToken(
        activity: FragmentActivity,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(activity, callback) {
        repository.paymentWithToken(details) { t, p -> activity.runChallenge(t, p) }
    }

    /**
     * Performs a token pre-authorisation using a previously saved card.
     *
     * @param activity The [FragmentActivity] used to present the 3DS2 challenge if required.
     * @param details  Token and billing details for the transaction.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun preAuthWithToken(
        activity: FragmentActivity,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(activity, callback) {
        repository.preAuthWithToken(details) { t, p -> activity.runChallenge(t, p) }
    }

    /**
     * Saves a card without performing a payment (register card flow).
     *
     * @param activity The [FragmentActivity] used to present the 3DS2 challenge if required.
     * @param details  Card and billing details.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun save(
        activity: FragmentActivity,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(activity, callback) {
        repository.save(details) { t, p -> activity.runChallenge(t, p) }
    }

    /**
     * Checks a card without performing a payment.
     *
     * @param activity The [FragmentActivity] used to present the 3DS2 challenge if required.
     * @param details  Card and billing details.
     * @param callback Invoked on the main thread with the transaction result.
     */
    fun check(
        activity: FragmentActivity,
        details: TransactionDetails,
        callback: JudoCardTransactionCallback,
    ) = execute(activity, callback) {
        repository.check(details) { t, p -> activity.runChallenge(t, p) }
    }

    private fun execute(
        activity: FragmentActivity,
        callback: JudoCardTransactionCallback,
        block: suspend () -> JudoPaymentResult,
    ) {
        activity.lifecycleScope.launch { callback.onResult(block()) }
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
        ): JudoCardTransactionClient = JudoCardTransactionClient(CardTransactionRepository.create(context, judo))
    }
}

// ---------------------------------------------------------------------------
// Kotlin suspend extensions — coroutine-friendly wrappers over the callback API
// ---------------------------------------------------------------------------

/**
 * Suspend variant of [JudoCardTransactionClient.payment] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.payment(
    activity: FragmentActivity,
    details: TransactionDetails,
): JudoPaymentResult =
    suspendCancellableCoroutine { cont ->
        payment(activity, details) { cont.resumeWith(Result.success(it)) }
    }

/**
 * Suspend variant of [JudoCardTransactionClient.preAuth] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.preAuth(
    activity: FragmentActivity,
    details: TransactionDetails,
): JudoPaymentResult =
    suspendCancellableCoroutine { cont ->
        preAuth(activity, details) { cont.resumeWith(Result.success(it)) }
    }

/**
 * Suspend variant of [JudoCardTransactionClient.paymentWithToken] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.paymentWithToken(
    activity: FragmentActivity,
    details: TransactionDetails,
): JudoPaymentResult =
    suspendCancellableCoroutine { cont ->
        paymentWithToken(activity, details) { cont.resumeWith(Result.success(it)) }
    }

/**
 * Suspend variant of [JudoCardTransactionClient.preAuthWithToken] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.preAuthWithToken(
    activity: FragmentActivity,
    details: TransactionDetails,
): JudoPaymentResult =
    suspendCancellableCoroutine { cont ->
        preAuthWithToken(activity, details) { cont.resumeWith(Result.success(it)) }
    }

/**
 * Suspend variant of [JudoCardTransactionClient.save] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.save(
    activity: FragmentActivity,
    details: TransactionDetails,
): JudoPaymentResult =
    suspendCancellableCoroutine { cont ->
        save(activity, details) { cont.resumeWith(Result.success(it)) }
    }

/**
 * Suspend variant of [JudoCardTransactionClient.check] for Kotlin coroutine callers.
 */
suspend fun JudoCardTransactionClient.check(
    activity: FragmentActivity,
    details: TransactionDetails,
): JudoPaymentResult =
    suspendCancellableCoroutine { cont ->
        check(activity, details) { cont.resumeWith(Result.success(it)) }
    }
