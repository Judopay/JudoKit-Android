package com.judopay.ui.cardverification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judopay.api.JudoApiService
import com.judopay.api.model.response.CardVerificationResult
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import kotlinx.coroutines.launch

internal class IdealViewModelFactory(
    private val service: JudoApiService,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == CardVerificationViewModel::class.java) {
            CardVerificationViewModel(service, application) as T
        } else super.create(modelClass)
    }
}

class CardVerificationViewModel(val service: JudoApiService, application: Application) : AndroidViewModel(application) {

    val judoApiCallResult = MutableLiveData<JudoApiCallResult<Receipt>>()
    val isLoading = MutableLiveData<Boolean>()

    fun complete3DSecure(
        receiptId: String,
        cardVerificationResult: CardVerificationResult
    ) = viewModelScope.launch {
        isLoading.postValue(true)
        val response = service.complete3dSecure(receiptId, cardVerificationResult)
        judoApiCallResult.postValue(response)
        isLoading.postValue(false)
    }
}
