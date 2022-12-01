package com.judopay.judokit.android

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.PaymentData
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.request.toJudoResult
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.model.TransactionDetails
import com.judopay.judokit.android.model.isGooglePayWidget
import com.judopay.judokit.android.model.isPaymentMethodsWidget
import com.judopay.judokit.android.service.JudoGooglePayService
import com.judopay.judokit.android.ui.common.toGooglePayRequest
import com.judopay.judokit.android.ui.common.toPreAuthGooglePayRequest
import kotlinx.coroutines.launch
import retrofit2.await

// view-model actions
sealed class JudoSharedAction {
    data class LoadGPayPaymentDataSuccess(val paymentData: PaymentData) : JudoSharedAction()
    data class LoadGPayPaymentDataError(val errorMessage: String) : JudoSharedAction()
    object LoadGPayPaymentDataUserCancelled : JudoSharedAction()
    object LoadGPayPaymentData : JudoSharedAction()
}

// view-model custom factory
internal class JudoSharedViewModelFactory(
    private val judo: Judo,
    private val googlePayService: JudoGooglePayService,
    private val judoApiService: JudoApiService,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == JudoSharedViewModel::class.java) {
            JudoSharedViewModel(judo, googlePayService, judoApiService, application) as T
        } else super.create(modelClass)
    }
}

class JudoSharedViewModel(
    private val judo: Judo,
    private val googlePayService: JudoGooglePayService,
    private val judoApiService: JudoApiService,
    application: Application
) : AndroidViewModel(application) {

    private val resources = application.resources
    // used to share a card payment result between fragments (card input / payment methods)
    val paymentResult = MutableLiveData<JudoPaymentResult>()

    val bankPaymentResult = MutableLiveData<JudoPaymentResult>()
    val paymentMethodsResult = MutableLiveData<JudoPaymentResult>()
    // used to pass security code from card entry to payment methods screen
    val cardEntryToPaymentMethodResult = MutableLiveData<TransactionDetails.Builder>()
    // used to share the GooglePay payment result between this activity and the payment methods fragment
    val paymentMethodsGooglePayResult = MutableLiveData<JudoPaymentResult>()

    // used to persist all captured errors and send to merchant on back press
    val error = JudoError()

    fun send(action: JudoSharedAction) = when (action) {
        is JudoSharedAction.LoadGPayPaymentData -> onLoadGPayPaymentData()
        is JudoSharedAction.LoadGPayPaymentDataSuccess -> onLoadGPayPaymentDataSuccess(action.paymentData)
        is JudoSharedAction.LoadGPayPaymentDataError -> onLoadGPayPaymentDataError(action.errorMessage)
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
        dispatchResult(JudoPaymentResult.Error(JudoError.googlePayNotSupported(resources, errorMessage)))
    }

    private fun onLoadGPayPaymentDataSuccess(paymentData: PaymentData) {
        try {
            if (judo.paymentWidgetType == PaymentWidgetType.SERVER_TO_SERVER_PAYMENT_METHODS) {
                val googlePayRequest = paymentData.toGooglePayRequest(judo)
                dispatchResult(JudoPaymentResult.Success(googlePayRequest.toJudoResult()))
            } else {
                sendGPayRequest(paymentData)
            }
        } catch (exception: Throwable) {
            onLoadGPayPaymentDataError(exception.message ?: "Unknown error")
        }
    }

    private fun onLoadGPayPaymentDataUserCancelled() {
        dispatchResult(JudoPaymentResult.UserCancelled())
    }

    @Throws(IllegalStateException::class)
    private fun sendGPayRequest(paymentData: PaymentData) = viewModelScope.launch {
        val result = when (judo.paymentWidgetType) {
            PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> judoApiService.preAuthGooglePayPayment(
                paymentData.toPreAuthGooglePayRequest(judo)
            ).await()
            PaymentWidgetType.GOOGLE_PAY,
            PaymentWidgetType.PAYMENT_METHODS -> judoApiService.googlePayPayment(
                paymentData.toGooglePayRequest(judo)
            ).await()
            else -> throw IllegalStateException("Unexpected payment widget type: ${judo.paymentWidgetType}")
        }

        dispatchResult(result.toJudoPaymentResult(resources))
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
