package com.judopay.judokit.android.service

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.ui.common.toIsReadyToPayRequest
import com.judopay.judokit.android.ui.common.toPaymentDataRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class JudoGooglePayService(
    private val paymentsClient: PaymentsClient,
    private val judo: Judo,
    private val launcher: ActivityResultLauncher<IntentSenderRequest>,
) {
    fun loadGooglePayPaymentData(
        onSuccess: (PaymentData) -> Unit,
        onError: (String) -> Unit,
    ) {
        judo.googlePayConfiguration?.let {
            val request = it.toPaymentDataRequest(judo)
            paymentsClient
                .loadPaymentData(request)
                .addOnSuccessListener { paymentData -> onSuccess(paymentData) }
                .addOnFailureListener { e ->
                    if (e is ResolvableApiException) {
                        launcher.launch(IntentSenderRequest.Builder(e.resolution).build())
                    } else {
                        onError(e.message ?: "Unknown error")
                    }
                }
        }
    }

    suspend fun checkIfGooglePayIsAvailable(): Boolean =
        suspendCancellableCoroutine { cont ->
            if (judo.googlePayConfiguration != null) {
                val isReadyToPayRequest = judo.googlePayConfiguration.toIsReadyToPayRequest(judo)
                paymentsClient.isReadyToPay(isReadyToPayRequest).addOnCompleteListener { task ->
                    try {
                        val isGooglePayAvailable = task.isSuccessful
                        val exception = task.exception

                        if (exception != null) {
                            cont.resumeWithException(exception)
                        } else {
                            cont.resume(isGooglePayAvailable)
                        }
                    } catch (exception: ApiException) {
                        cont.resumeWithException(exception)
                    }
                }
            } else {
                cont.resumeWithException(IllegalStateException("Invalid GooglePay configuration object."))
            }
        }
}
