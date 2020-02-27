package com.judopay.ui.paymentmethods

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.judopay.Judo
import com.judopay.JudoActivity
import com.judopay.api.JudoApiService
import com.judopay.api.error.ExceptionHandler
import com.judopay.api.model.request.Address
import com.judopay.api.model.request.PaymentRequest
import com.judopay.api.model.request.TokenRequest
import com.judopay.api.model.response.Receipt
import com.judopay.isPreAuthEnabled
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class PaymentMethodsViewModel constructor(
    private val service: JudoApiService
) : ViewModel(), CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    private val handler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        ExceptionHandler.handleException(throwable, fragment.requireActivity() as JudoActivity)
    }

    private lateinit var fragment: Fragment
    private val _receipt = MutableLiveData<Receipt>()

    val receipt: LiveData<Receipt> = _receipt

    fun pay(judo: Judo) {
        _receipt.apply {
            launch {
                value = withContext(Dispatchers.IO) {
                    with(judo) {
                        when (isTokenPayment) {
                            true -> {
                                val tokenRequest = buildTokenRequest(judo)
                                if (fragment.isPreAuthEnabled) {
                                    service.tokenPreAuth(tokenRequest)
                                } else {
                                    service.tokenPayment(tokenRequest)
                                }
                            }
                            false -> {
                                val paymentRequest = buildPaymentRequest(judo)
                                if (fragment.isPreAuthEnabled) {
                                    service.preAuth(paymentRequest)
                                } else {
                                    service.payment(paymentRequest)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun buildPaymentRequest(judo: Judo) = with(judo) {
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
    }

    private fun buildTokenRequest(judo: Judo) = with(judo) {
        TokenRequest.Builder()
            .setUniqueRequest(false)
            .setYourPaymentReference(reference.paymentReference)
            .setAmount(amount.amount)
            .setCurrency(amount.currency.name)
            .setJudoId(judoId)
            .setYourConsumerReference(reference.consumerReference)
            .setYourPaymentMetaData(mapOf())
            .setCardLastFour("3436")
            .setCardToken("TOKEN")
            .setCardType(1)
            .setAddress(Address.Builder().build())
            .build()
    }

    private fun launch(block: suspend CoroutineScope.() -> Unit) {
        launch(handler) {
            block.invoke(this)
        }
    }

    fun takeView(paymentMethodsFragment: Fragment) {
        fragment = paymentMethodsFragment
    }

    class PaymentMethodsViewModelFactory constructor(
        private val service: JudoApiService
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            PaymentMethodsViewModel(service) as T
    }
}
