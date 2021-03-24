package com.judopay.judokit.android.service

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentsClient
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.LOAD_GPAY_PAYMENT_DATA_REQUEST_CODE
import com.judopay.judokit.android.ui.common.toIsReadyToPayRequest
import com.judopay.judokit.android.ui.common.toPaymentDataRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class JudoGooglePayService(
    private val paymentsClient: PaymentsClient,
    private val callbackActivity: AppCompatActivity,
    private val judo: Judo
) {

    fun loadGooglePayPaymentData() {
        judo.googlePayConfiguration?.let {
            val request = it.toPaymentDataRequest(judo)
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request),
                callbackActivity,
                LOAD_GPAY_PAYMENT_DATA_REQUEST_CODE
            )
        }
    }

    suspend fun checkIfGooglePayIsAvailable(): Boolean = suspendCancellableCoroutine { cont ->
        if (judo.googlePayConfiguration != null) {
            val isReadyToPayRequest = judo.googlePayConfiguration.toIsReadyToPayRequest(judo)
            paymentsClient.isReadyToPay(isReadyToPayRequest).addOnCompleteListener { task ->
                try {
                    val isGooglePayAvailable = task.isSuccessful
                    cont.resume(isGooglePayAvailable)
                } catch (exception: ApiException) {
                    cont.resumeWithException(exception)
                }
            }
        } else {
            cont.resumeWithException(IllegalStateException("Invalid GooglePay configuration object."))
        }
    }
}
