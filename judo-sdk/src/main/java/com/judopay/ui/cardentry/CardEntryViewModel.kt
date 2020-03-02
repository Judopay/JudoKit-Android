package com.judopay.ui.cardentry

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.judopay.Judo
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.api.model.request.SaveCardRequest
import com.judopay.api.model.response.JudoApiCallResult
import kotlinx.coroutines.Dispatchers

class CardEntryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    fun send(judo: Judo, request: SaveCardRequest) = liveData(Dispatchers.IO) {
        val service = JudoApiServiceFactory.createApiService(context, judo)

        when (val response = service.saveCard(request)) {
            is JudoApiCallResult.Success -> {
                Log.e("CardEntryViewModel S", response.toString())
                emit(response.data)
            }
            is JudoApiCallResult.NetworkError -> {
                Log.e("CardEntryViewModel NE", response.toString())
            }
            is JudoApiCallResult.Failure -> {
                Log.e("CardEntryViewModel F", response.toString())
            }
        }

    }
}
