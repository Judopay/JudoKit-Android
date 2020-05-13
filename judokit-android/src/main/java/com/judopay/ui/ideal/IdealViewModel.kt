package com.judopay.ui.ideal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judopay.Judo
import com.judopay.api.JudoApiService
import com.judopay.api.model.request.IdealSaleRequest
import com.judopay.api.model.response.IdealSaleResponse
import com.judopay.api.model.response.IdealSaleStatusResponse
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.OrderStatus
import com.judopay.toMap
import java.math.BigDecimal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// view-model custom factory to inject the `judo` configuration object
internal class IdealViewModelFactory(
    private val bic: String,
    private val judo: Judo,
    private val service: JudoApiService,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == IdealViewModel::class.java) {
            IdealViewModel(bic, judo, service, application) as T
        } else super.create(modelClass)
    }
}

private const val DELAY_IN_SECONDS = 130L
private const val MILLISECONDS = 1000L
private const val REQUEST_DELAY = 5000L
private const val TIMEOUT = DELAY_IN_SECONDS * MILLISECONDS

class IdealViewModel(
    val bic: String,
    val judo: Judo,
    val service: JudoApiService,
    application: Application
) :
    AndroidViewModel(application) {
    val saleStatusCallResult = MutableLiveData<JudoApiCallResult<IdealSaleStatusResponse>>()
    val saleCallResult = MutableLiveData<JudoApiCallResult<IdealSaleResponse>>()
    val isLoading = MutableLiveData<Boolean>()
    val isRequestDelayed = MutableLiveData<Boolean>()
    private lateinit var orderId: String

    fun completeIdealPayment() = viewModelScope.launch {
        isLoading.postValue(true)
        isRequestDelayed.postValue(false)
        var timeout = TIMEOUT
        while (timeout != 0L) {
            if (timeout != DELAY_IN_SECONDS * MILLISECONDS) {
                delay(REQUEST_DELAY)
            }

            when (val response = service.status(orderId)) {
                is JudoApiCallResult.Success -> {
                    if (response.data != null)
                        when (response.data.orderDetails.orderStatus) {
                            OrderStatus.SUCCEEDED -> {
                                timeout = 0L
                                saleStatusCallResult.postValue(response)
                            }
                            OrderStatus.PENDING -> {
                                timeout -= REQUEST_DELAY
                                if (timeout <= TIMEOUT / 2)
                                    isRequestDelayed.postValue(true)
                                if (timeout == 0L)
                                    saleStatusCallResult.postValue(response)
                            }
                            else -> {
                                timeout = 0L
                                saleStatusCallResult.postValue(response)
                            }
                        }
                }
                is JudoApiCallResult.Failure -> {
                    timeout = 0L
                    saleStatusCallResult.postValue(response)
                }
            }
        }
        isLoading.postValue(false)
    }

    fun payWithSelectedBank() = viewModelScope.launch {
        isLoading.postValue(true)
        val request = IdealSaleRequest.Builder()
            .setAmount(BigDecimal(judo.amount.amount))
            .setMerchantConsumerReference(judo.reference.consumerReference)
            .setMerchantPaymentReference(judo.reference.paymentReference)
            .setPaymentMetadata(judo.reference.metaData?.toMap())
            .setSiteId(judo.siteId)
            .setBic(bic)
            .build()

        val response = service.sale(request)

        saleCallResult.postValue(response)

        if (response is JudoApiCallResult.Success && response.data != null) {
            orderId = response.data.orderId
        }

        isLoading.postValue(false)
    }
}
