package com.judopay

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import com.judopay.api.error.ApiError
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.api.model.request.GooglePayRequest
import com.judopay.api.model.response.toJudoPaymentResult
import com.judopay.model.JudoPaymentResult
import com.judopay.model.PaymentWidgetType
import com.judopay.model.isGooglePayWidget
import com.judopay.model.isPaymentMethodsWidget
import com.judopay.ui.common.toGooglePayRequest
import com.judopay.ui.common.toPaymentDataRequest
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
    private var paymentsClient: PaymentsClient,
    private val activity: AppCompatActivity
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == JudoSharedViewModel::class.java) {
            JudoSharedViewModel(paymentsClient, activity) as T
        } else super.create(modelClass)
    }
}

class JudoSharedViewModel(
    private val paymentsClient: PaymentsClient,
    private val activity: AppCompatActivity //TODO: this should not be here
) : ViewModel() {

    private val service = JudoApiServiceFactory.createApiService(activity, activity.judo)

    val paymentResult = MutableLiveData<JudoPaymentResult>()
    val threeDSecureResult = MutableLiveData<JudoPaymentResult>()
    val paymentMethodsGooglePayResult = MutableLiveData<JudoPaymentResult>()

    fun send(action: JudoSharedAction) = when (action) {
        is JudoSharedAction.LoadGPayPaymentData -> onLoadGPayPaymentData()
        is JudoSharedAction.LoadGPayPaymentDataSuccess -> onLoadGPayPaymentDataSuccess(action.paymentData)
        is JudoSharedAction.LoadGPayPaymentDataError -> onLoadGPayPaymentDataError(action.errorMessage)
        is JudoSharedAction.LoadGPayPaymentDataUserCancelled -> onLoadGPayPaymentDataUserCancelled()
    }

    private fun onLoadGPayPaymentData() {
        val judo = activity.judo
        val config = judo.googlePayConfiguration

        config?.let {
            val request = it.toPaymentDataRequest(judo)
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request),
                activity,
                LOAD_GPAY_PAYMENT_DATA_REQUEST_CODE
            )
        }
    }

    private fun onLoadGPayPaymentDataError(errorMessage: String) {
        dispatchResult(JudoPaymentResult.Error(ApiError(-1, -1, errorMessage)))
    }

    private fun onLoadGPayPaymentDataSuccess(paymentData: PaymentData) {
        try {
            sendRequest(paymentData.toGooglePayRequest(activity.judo))
        } catch (exception: Throwable) {
            onLoadGPayPaymentDataError(exception.message ?: "Unknown error")
        }
    }

    private fun onLoadGPayPaymentDataUserCancelled() {
        dispatchResult(JudoPaymentResult.UserCancelled)
    }

    @Throws(IllegalStateException::class)
    private fun sendRequest(googlePayRequest: GooglePayRequest) = viewModelScope.launch {
        val result = when (activity.judo.paymentWidgetType) {
            PaymentWidgetType.PRE_AUTH_GOOGLE_PAY,
            PaymentWidgetType.PRE_AUTH_PAYMENT_METHODS -> service.googlePayPayment(googlePayRequest)
            PaymentWidgetType.GOOGLE_PAY,
            PaymentWidgetType.PAYMENT_METHODS -> service.preAuthGooglePayPayment(googlePayRequest)
            else -> throw IllegalStateException("Unexpected payment widget type: ${activity.judo.paymentWidgetType}")
        }

        dispatchResult(result.toJudoPaymentResult())
    }

    private fun dispatchResult(result: JudoPaymentResult) {
        val type = activity.judo.paymentWidgetType

        val liveData = when {
            type.isGooglePayWidget -> paymentResult
            type.isPaymentMethodsWidget -> paymentMethodsGooglePayResult
            else -> null
        }

        liveData?.postValue(result)
    }
}
