package com.judopay.ui.paymentmethods

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.judopay.Judo
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.api.model.request.Address
import com.judopay.api.model.request.TokenRequest
import com.judopay.db.JudoRoomDatabase
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.db.repository.TokenizedCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentMethodsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TokenizedCardRepository
    val allCards: LiveData<List<TokenizedCardEntity>>

    private val context = application

    init {
        val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
        repository = TokenizedCardRepository(tokenizedCardDao)
        allCards = repository.allCards
    }

    fun insert(card: TokenizedCardEntity) = viewModelScope.launch {
        repository.insert(card)
    }

    fun deleteCardWithId(id: Int) = viewModelScope.launch {
        repository.deleteCardWithId(id)
    }

    fun payWithToken(judo: Judo, token: String, ending: String) = liveData(Dispatchers.IO) {
        val service = JudoApiServiceFactory.createApiService(context, judo)
        val request = TokenRequest.Builder()
                .setYourPaymentReference(judo.reference.paymentReference)
                .setAmount(judo.amount.amount)
                .setCurrency(judo.amount.currency.name)
                .setJudoId(judo.judoId)
                .setYourConsumerReference(judo.reference.consumerReference)
                .setYourPaymentMetaData(mapOf())
                .setCardLastFour(ending)
                .setCardToken(token)
                .setCardType(1)
                .setAddress(Address.Builder().build())
                .build()

        val response = service.tokenPayment(request)
        emit(response)
    }
}

//    private fun buildPaymentRequest(judo: Judo) = with(judo) {
//        PaymentRequest.Builder()
//            .setUniqueRequest(false)
//            .setYourPaymentReference(reference.paymentReference)
//            .setAmount(amount.amount)
//            .setCurrency(amount.currency.name)
//            .setJudoId(judoId)
//            .setYourConsumerReference(reference.consumerReference)
//            .setYourPaymentMetaData(mapOf())
//            .setAddress(Address.Builder().build())
//            .setCardNumber("4976000000003436")
//            .setCv2("452")
//            .setExpiryDate("12/20")
//            .build()
//    }
//
//    private fun buildTokenRequest(judo: Judo) = with(judo) {
//        TokenRequest.Builder()
//            .setUniqueRequest(false)
//            .setYourPaymentReference(reference.paymentReference)
//            .setAmount(amount.amount)
//            .setCurrency(amount.currency.name)
//            .setJudoId(judoId)
//            .setYourConsumerReference(reference.consumerReference)
//            .setYourPaymentMetaData(mapOf())
//            .setCardLastFour("3436")
//            .setCardToken("TOKEN")
//            .setCardType(1)
//            .setAddress(Address.Builder().build())
//            .build()
//    }
