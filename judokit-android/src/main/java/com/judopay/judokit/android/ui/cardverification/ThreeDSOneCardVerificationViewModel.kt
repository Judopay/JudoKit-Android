package com.judopay.judokit.android.ui.cardverification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.response.CardVerificationResult
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.Receipt
import kotlinx.coroutines.launch
import retrofit2.await

internal class ThreeDSOneCardVerificationViewModelFactory(
    private val service: JudoApiService,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == ThreeDSOneCardVerificationViewModel::class.java) {
            ThreeDSOneCardVerificationViewModel(service, application) as T
        } else super.create(modelClass)
    }
}

class ThreeDSOneCardVerificationViewModel(val service: JudoApiService, application: Application) : AndroidViewModel(application) {

    val judoApiCallResult = MutableLiveData<JudoApiCallResult<Receipt>>()
    val isLoading = MutableLiveData<Boolean>()

    fun complete3DSecure(
        receiptId: String,
        cardVerificationResult: CardVerificationResult
    ) = viewModelScope.launch {
        isLoading.postValue(true)
        val response = service.complete3dSecure(receiptId, cardVerificationResult).await()
        judoApiCallResult.postValue(response)
        isLoading.postValue(false)
    }
}
