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

    val judoApiCallResult = MutableLiveData<JudoApiCallResult<Receipt>>()
    val isLoading = MutableLiveData<Boolean>()
    fun complete3DSecure(
        judo: Judo,
        receiptId: String,
        cardVerificationResult: CardVerificationResult
    ) = viewModelScope.launch {
        isLoading.postValue(true)
        val service = JudoApiServiceFactory.createApiService(getApplication(), judo)
        val response = service.complete3dSecure(receiptId, cardVerificationResult)
        judoApiCallResult.postValue(response)
        isLoading.postValue(false)
    }
}