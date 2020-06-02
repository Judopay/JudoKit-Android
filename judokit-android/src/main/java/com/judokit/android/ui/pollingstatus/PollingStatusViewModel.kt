package com.judokit.android.ui.pollingstatus

import android.app.Application
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
import com.judokit.android.service.polling.PollingResult
import com.judokit.android.service.polling.PollingService
import com.judokit.android.toMap
import kotlinx.coroutines.launch

sealed class PollingAction {
    object PayWithPayByBank : PollingAction()
    data class StartPolling(val orderId: String) : PollingAction()
    object CancelPolling : PollingAction()
    object ResetPolling : PollingAction()
    object RetryPolling : PollingAction()
}

internal class PollingStatusViewModelFactory(
    private val service: JudoApiService,
    private val pollingService: PollingService,
    private val application: Application,
    private val judo: Judo
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == PollingStatusViewModel::class.java) {
            PollingStatusViewModel(service, pollingService, application, judo) as T
        } else super.create(modelClass)
    }
}

class PollingStatusViewModel(
    private val service: JudoApiService,
    private val pollingService: PollingService,
    application: Application,
    private val judo: Judo
) : AndroidViewModel(application) {

    val payByBankResult = MutableLiveData<JudoApiCallResult<BankSaleResponse>>()
    val saleStatusResult = MutableLiveData<PollingResult<BankSaleStatusResponse>>()

    fun send(action: PollingAction) {
        when (action) {
            is PollingAction.PayWithPayByBank -> payWithPayByBank()
            is PollingAction.StartPolling -> startPolling(action.orderId)
            is PollingAction.CancelPolling -> pollingService.cancel()
            is PollingAction.ResetPolling -> pollingService.reset()
            is PollingAction.RetryPolling -> viewModelScope.launch {
                pollingService.retry()
            }
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
            .build()

        val response = service.sale(request)

        payByBankResult.postValue(response)
    }
}
