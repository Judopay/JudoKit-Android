package com.judopay

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.PaymentData
import com.judopay.api.JudoApiService
import com.judopay.api.error.ApiError
import com.judopay.api.model.request.GooglePayRequest
import com.judopay.api.model.response.toJudoPaymentResult
import com.judopay.model.JudoPaymentResult
import com.judopay.model.PaymentWidgetType
import com.judopay.model.isGooglePayWidget
import com.judopay.model.isPaymentMethodsWidget
import com.judopay.service.JudoGooglePayService
import com.judopay.ui.common.toGooglePayRequest
import kotlinx.coroutines.launch

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

    // used to share a 3D secure result between fragments (card input / payment methods / card verification)
    val threeDSecureResult = MutableLiveData<JudoPaymentResult>()

    // used to share an iDEAL transaction result
    val idealResult = MutableLiveData<JudoPaymentResult>()

    // used to share the GooglePay payment result between this activity and the payment methods fragment
    val paymentMethodsGooglePayResult = MutableLiveData<JudoPaymentResult>()

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
                    onLoadGPayPaymentDataError("GooglePay is not available on this device")
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
        dispatchResult(JudoPaymentResult.Error(ApiError(-1, -1, errorMessage)))
    }

    private fun onLoadGPayPaymentDataSuccess(paymentData: PaymentData) {
        try {
            sendRequest(paymentData.toGooglePayRequest(judo))
        } catch (exception: Throwable) {
            onLoadGPayPaymentDataError(exception.message ?: "Unknown error")
        }
    }

    private fun onLoadGPayPaymentDataUserCancelled() {
        dispatchResult(JudoPaymentResult.UserCancelled)
    }

    @Throws(IllegalStateException::class)
    private fun sendRequest(googlePayRequest: GooglePayRequest) = viewModelScope.launch {
        val result = when (judo.paymentWidgetType) {
            PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> judoApiService.googlePayPayment(
                googlePayRequest
            )
            PaymentWidgetType.GOOGLE_PAY,
            PaymentWidgetType.PAYMENT_METHODS -> judoApiService.preAuthGooglePayPayment(
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
            type.isPaymentMethodsWidget -> paymentMethodsGooglePayResult
            else -> null
        }

        liveData?.postValue(result)
    }
}
