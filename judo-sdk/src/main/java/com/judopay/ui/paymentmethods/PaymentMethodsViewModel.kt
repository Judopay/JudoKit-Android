package com.judopay.ui.paymentmethods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.judopay.Judo
import com.judopay.api.JudoApiService
import com.judopay.api.error.ExceptionHandler
import com.judopay.api.model.request.Address
import com.judopay.api.model.request.PaymentRequest
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class PaymentMethodsViewModel private constructor(
    private val service: JudoApiService
) : ViewModel(), CoroutineScope {
    private val handler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        ExceptionHandler.handleException(throwable)
    }
    private val job = SupervisorJob()
    fun pay(judo: Judo) {
        GlobalScope.launch(handler) {
            with(judo) {
                val receipt = service.payment(
                    PaymentRequest.Builder()
                        .setUniqueRequest(false)
                        .setYourPaymentReference(reference.paymentReference)
                        .setAmount(amount.amount)
                        .setCurrency(amount.currency.name)
                        .setJudoId(judoId)
                        .setYourConsumerReference(reference.consumerReference)
                        .setYourPaymentMetaData(mapOf())
                        .setAddress(Address.Builder().build())
                        .setCardNumber("4976000000003436")
                        .setCv2("452")
                        .setExpiryDate("12/20")
                        .build()
                )
            }
        }
    }

    class PaymentMethodsViewModelFactory constructor(
        private val service: JudoApiService
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            PaymentMethodsViewModel(service) as T
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
}
