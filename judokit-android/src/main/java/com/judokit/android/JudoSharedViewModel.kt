package com.judokit.android

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.PaymentData
import com.judokit.android.api.JudoApiService
import com.judokit.android.api.model.request.GooglePayRequest
import com.judokit.android.api.model.request.toJudoResult
import com.judokit.android.api.model.response.toJudoPaymentResult
import com.judokit.android.model.CardScanningResult
import com.judokit.android.model.INTERNAL_ERROR
import com.judokit.android.model.JudoError
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.model.isGooglePayWidget
import com.judokit.android.model.isPaymentMethodsWidget
import com.judokit.android.service.JudoGooglePayService
import com.judokit.android.ui.common.toGooglePayRequest
import kotlinx.coroutines.launch

// view-model actions
sealed class JudoSharedAction {
    data class LoadGPayPaymentDataSuccess(val paymentData: PaymentData) : JudoSharedAction()
    data class LoadGPayPaymentDataError(val errorMessage: String) : JudoSharedAction()
    data class ScanCardResult(val result: CardScanningResult) : JudoSharedAction()
    object LoadGPayPaymentDataUserCancelled : JudoSharedAction()
    object LoadGPayPaymentData : JudoSharedAction()
}

// view-model custom factory
internal class JudoSharedViewModelFactory(
    private val judo: Judo,
    private val googlePayService: JudoGooglePayService,
    private val judoApiService: JudoApiService
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == JudoSharedViewModel::class.java) {
            JudoSharedViewModel(judo, googlePayService, judoApiService) as T
        } else super.create(modelClass)
    }
}

class JudoSharedViewModel(
    private val judo: Judo,
    private val googlePayService: JudoGooglePayService,
    private val judoApiService: JudoApiService
) : ViewModel() {

    // used to share a card payment result between fragments (card input / payment methods)
    val paymentResult = MutableLiveData<JudoPaymentResult>()

    val bankPaymentResult = MutableLiveData<JudoPaymentResult>()
    val paymentMethodsResult = MutableLiveData<JudoPaymentResult>()

    // used to share the GooglePay payment result between this activity and the payment methods fragment
    val paymentMethodsGooglePayResult = MutableLiveData<JudoPaymentResult>()

    // used to share a scan card result between fragments (card input)
    val scanCardResult = MutableLiveData<CardScanningResult>()

    // used to persist all captured errors and send to merchant on back press
    val error = JudoError()

    fun send(action: JudoSharedAction) = when (action) {
        is JudoSharedAction.LoadGPayPaymentData -> onLoadGPayPaymentData()
        is JudoSharedAction.LoadGPayPaymentDataSuccess -> onLoadGPayPaymentDataSuccess(action.paymentData)
        is JudoSharedAction.LoadGPayPaymentDataError -> onLoadGPayPaymentDataError(action.errorMessage)
        is JudoSharedAction.ScanCardResult -> onScanCardSuccess(action.result)
        is JudoSharedAction.LoadGPayPaymentDataUserCancelled -> onLoadGPayPaymentDataUserCancelled()
    }

    private fun onLoadGPayPaymentData() {
        viewModelScope.launch {
            try {
                val isAvailable = googlePayService.checkIfGooglePayIsAvailable()
                if (isAvailable) {
                    googlePayService.loadGooglePayPaymentData()
                } else {
                    onLoadGPayPaymentDataError("GooglePay is not supported on your device")
                }
            } catch (exception: Exception) {
                when (exception) {
                    is IllegalStateException,
                    is ApiException -> {
                        onLoadGPayPaymentDataError(exception.message ?: "Unknown error")
                    }
                    else -> throw exception
                }
            }
        }
    }

    private fun onLoadGPayPaymentDataError(errorMessage: String) {
        dispatchResult(JudoPaymentResult.Error(JudoError(INTERNAL_ERROR, errorMessage)))
    }

    private fun onScanCardSuccess(result: CardScanningResult) {
        scanCardResult.postValue(result)
    }

    private fun onLoadGPayPaymentDataSuccess(paymentData: PaymentData) {
        try {
            val googlePayRequest = paymentData.toGooglePayRequest(judo)
            if (judo.paymentWidgetType == PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS) {
                dispatchResult(JudoPaymentResult.Success(googlePayRequest.toJudoResult()))
            } else {
                sendRequest(googlePayRequest)
            }
        } catch (exception: Throwable) {
            onLoadGPayPaymentDataError(exception.message ?: "Unknown error")
        }
    }

    private fun onLoadGPayPaymentDataUserCancelled() {
        dispatchResult(JudoPaymentResult.UserCancelled())
    }

    @Throws(IllegalStateException::class)
    private fun sendRequest(googlePayRequest: GooglePayRequest) = viewModelScope.launch {
        val result = when (judo.paymentWidgetType) {
            PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> judoApiService.preAuthGooglePayPayment(
                googlePayRequest
            )
            PaymentWidgetType.GOOGLE_PAY,
            PaymentWidgetType.PAYMENT_METHODS -> judoApiService.googlePayPayment(
                googlePayRequest
            )
            else -> throw IllegalStateException("Unexpected payment widget type: ${judo.paymentWidgetType}")
        }

        dispatchResult(result.toJudoPaymentResult())
    }

    private fun dispatchResult(result: JudoPaymentResult) {
        val type = judo.paymentWidgetType

        val liveData = when {
            type.isGooglePayWidget -> paymentResult
            type.isPaymentMethodsWidget -> paymentMethodsResult
            else -> null
        }

        liveData?.postValue(result)
    }
}
