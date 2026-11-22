package com.judopay.judokit.android.ui.common

import android.util.Log
import com.judopay.judokit.android.model.JudoPaymentResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * The payment-result effect stream of a ViewModel. A result emitted while nothing is collecting
 * [results] (e.g. a transaction finished after process death while the app is in the background)
 * waits until the UI collects it, instead of being dropped. Each result is received once.
 */
internal class PaymentResultEmitter(
    private val tag: String,
) {
    private val channel = Channel<JudoPaymentResult>(Channel.BUFFERED)
    val results: Flow<JudoPaymentResult> = channel.receiveAsFlow()

    fun emit(result: JudoPaymentResult) {
        if (channel.trySend(result).isFailure) {
            Log.w(tag, "Payment result effect dropped: ${result.javaClass.simpleName}")
        }
    }
}
