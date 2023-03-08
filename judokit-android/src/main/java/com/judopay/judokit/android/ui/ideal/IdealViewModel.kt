package com.judopay.judokit.android.ui.ideal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.request.IdealSaleRequest
import com.judopay.judokit.android.api.model.response.BankSaleStatusResponse
import com.judopay.judokit.android.api.model.response.IdealSaleResponse
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.service.polling.PollingResult
import com.judopay.judokit.android.service.polling.PollingService
import com.judopay.judokit.android.toMap
import kotlinx.coroutines.launch
import retrofit2.await

sealed class IdealAction {
    data class Initialise(val bic: String) : IdealAction()
    object CancelPolling : IdealAction()
    object ResetPolling : IdealAction()
    object RetryPolling : IdealAction()
    object StartPolling : IdealAction()
}

internal class IdealViewModelFactory(
    private val judo: Judo,
    private val service: JudoApiService,
    private val pollingService: PollingService,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass == IdealViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            IdealViewModel(judo, service, pollingService, application) as T
        } else {
            super.create(modelClass)
        }
    }
}

class IdealViewModel(
    val judo: Judo,
    val service: JudoApiService,
    private val pollingService: PollingService,
    application: Application
) :
    AndroidViewModel(application) {
    val saleCallResult = MutableLiveData<JudoApiCallResult<IdealSaleResponse>>()
    val saleStatusResult = MutableLiveData<PollingResult<BankSaleStatusResponse>>()
    val isLoading = MutableLiveData<Boolean>()
    val isRequestDelayed = MutableLiveData<Boolean>()
    private var myOrderId: String = ""

    fun send(action: IdealAction) {
        when (action) {
            is IdealAction.Initialise -> payWithSelectedBank(action.bic)
            is IdealAction.StartPolling -> startPolling()
            is IdealAction.CancelPolling -> pollingService.cancel()
            is IdealAction.ResetPolling -> pollingService.reset()
            is IdealAction.RetryPolling -> retry()
        }
    }

    private fun payWithSelectedBank(bic: String) = viewModelScope.launch {
        isLoading.postValue(true)
        val request = IdealSaleRequest.Builder()
            .setAmount(judo.amount.amount)
            .setMerchantConsumerReference(judo.reference.consumerReference)
            .setMerchantPaymentReference(judo.reference.paymentReference)
            .setPaymentMetadata(judo.reference.metaData?.toMap())
            .setJudoId(judo.judoId)
            .setBic(bic)
            .build()

        when (val response = service.sale(request).await()) {
            is JudoApiCallResult.Success -> {
                if (response.data != null) {
                    saleCallResult.postValue(response)
                    myOrderId = response.data.orderId
                } else {
                    saleCallResult.postValue(JudoApiCallResult.Failure())
                }
            }
            is JudoApiCallResult.Failure -> saleCallResult.postValue(response)
        }

        isLoading.postValue(false)
    }

    private fun startPolling() {
        viewModelScope.launch {
            pollingService.apply {
                orderId = myOrderId
                result = { saleStatusResult.postValue(it) }
            }
            pollingService.start()
        }
    }

    private fun retry() {
        viewModelScope.launch {
            pollingService.retry()
        }
    }
}
