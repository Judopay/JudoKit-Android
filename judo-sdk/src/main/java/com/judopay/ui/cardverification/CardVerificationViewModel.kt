package com.judopay.ui.cardverification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.judopay.Judo
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.api.model.response.CardVerificationResult
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import kotlinx.coroutines.launch

class CardVerificationViewModel(application: Application) : AndroidViewModel(application) {

    val receipt = MutableLiveData<Receipt>()
    val isLoading = MutableLiveData<Boolean>()
    fun complete3DSecure(
        judo: Judo,
        receiptId: String,
        cardVerificationResult: CardVerificationResult
    ) = viewModelScope.launch {
        isLoading.postValue(true)
        val service = JudoApiServiceFactory.createApiService(getApplication(), judo)
        when (val response = service.complete3dSecure(receiptId, cardVerificationResult)) {
            is JudoApiCallResult.Success -> receipt.postValue(response.data)
        }
        isLoading.postValue(false)
    }
}