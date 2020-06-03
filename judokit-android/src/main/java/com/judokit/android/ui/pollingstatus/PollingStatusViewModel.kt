package com.judokit.android.ui.pollingstatus

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judokit.android.Judo
import com.judokit.android.api.JudoApiService
import com.judokit.android.api.model.request.BankSaleRequest
import com.judokit.android.api.model.response.BankSaleResponse
import com.judokit.android.api.model.response.BankSaleStatusResponse
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.service.polling.PollingResult
import com.judokit.android.service.polling.PollingService
import com.judokit.android.toMap
import com.judokit.android.ui.paymentmethods.model.Event
import kotlinx.coroutines.launch

// TODO: Change to orderId
private const val ORDER_ID = "aptrId"

sealed class PollingAction {
    data class Initialise(val isDeepLinkCallback: Boolean) : PollingAction()
    object CancelPolling : PollingAction()
    object ResetPolling : PollingAction()
    object RetryPolling : PollingAction()
}

internal class PollingStatusViewModelFactory(
    private val service: JudoApiService,
    private val pollingService: PollingService,
    private val application: Application,
    private val judo: Judo,
    private val paymentWidgetType: PaymentWidgetType?
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == PollingStatusViewModel::class.java) {
            PollingStatusViewModel(
                service,
                pollingService,
                application,
                judo,
                paymentWidgetType
            ) as T
        } else super.create(modelClass)
    }
}

class PollingStatusViewModel(
    private val service: JudoApiService,
    private val pollingService: PollingService,
    application: Application,
    private val judo: Judo,
    private val paymentWidgetType: PaymentWidgetType?
) : AndroidViewModel(application) {

    val payByBankResult = MutableLiveData<JudoApiCallResult<BankSaleResponse>>()
    val saleStatusResult = MutableLiveData<PollingResult<BankSaleStatusResponse>>()
    val viewObserver = MutableLiveData<Event<Nothing>>()

    fun send(action: PollingAction) {
        when (action) {
            is PollingAction.Initialise -> initialise(action.isDeepLinkCallback)
            is PollingAction.CancelPolling -> pollingService.cancel()
            is PollingAction.ResetPolling -> pollingService.reset()
            is PollingAction.RetryPolling -> retry()
        }
    }

    private fun initialise(isDeepLinkCallback: Boolean) {
        when (paymentWidgetType ?: judo.paymentWidgetType) {
            PaymentWidgetType.PAY_BY_BANK_APP -> {
                val url = judo.pbbaConfiguration?.deepLinkURL
                if (url != null && isDeepLinkCallback) {
                    handleDeepLinkCallback(url)
                } else {
                    payWithPayByBank()
                }
            }
            else -> throw IllegalStateException("Unsupported PaymentWidgetType")
        }
    }

    private fun retry() {
        viewModelScope.launch {
            pollingService.retry()
        }
    }

    private fun handleDeepLinkCallback(url: Uri) {
        val orderId = url.getQueryParameter(ORDER_ID)
        if (orderId != null) {
            viewObserver.postValue(Event())
            startPolling(orderId)
        } else {
            saleStatusResult.postValue(PollingResult.CallFailure())
        }
    }

    private fun startPolling(myOrderId: String) {
        viewModelScope.launch {
            pollingService.apply {
                orderId = myOrderId
                result = { saleStatusResult.postValue(it) }
            }
            pollingService.start()
        }
    }

    private fun payWithPayByBank() = viewModelScope.launch {
        val request = BankSaleRequest.Builder()
            .setAmount(judo.amount.amount.toBigDecimalOrNull())
            .setMerchantPaymentReference(judo.reference.paymentReference)
            .setMerchantConsumerReference(judo.reference.consumerReference)
            .setSiteId(judo.siteId)
            .setMobileNumber(judo.pbbaConfiguration?.mobileNumber)
            .setEmailAddress(judo.pbbaConfiguration?.emailAddress)
            .setAppearsOnStatement(judo.pbbaConfiguration?.appearsOnStatement)
            .setPaymentMetadata(judo.reference.metaData?.toMap())
            .setMerchantRedirectUrl(judo.pbbaConfiguration?.deepLinkScheme)
            .build()

        val response = service.sale(request)

        payByBankResult.postValue(response)
    }
}
