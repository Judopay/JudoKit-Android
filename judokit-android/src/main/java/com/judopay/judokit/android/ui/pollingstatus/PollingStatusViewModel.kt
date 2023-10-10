package com.judopay.judokit.android.ui.pollingstatus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.judopay.judokit.android.service.polling.PollingService
import kotlinx.coroutines.launch

sealed class PollingAction {
    object CancelPolling : PollingAction()
    object ResetPolling : PollingAction()
    object RetryPolling : PollingAction()
}

internal class PollingStatusViewModelFactory(
    private val pollingService: PollingService,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass == PollingStatusViewModel::class.java) {
            @Suppress("UNCHECKED_CAST")
            PollingStatusViewModel(
                pollingService,
                application
            ) as T
        } else {
            super.create(modelClass)
        }
    }
}

class PollingStatusViewModel(
    private val pollingService: PollingService,
    application: Application
) : AndroidViewModel(application) {

    fun send(action: PollingAction) {
        when (action) {
            is PollingAction.CancelPolling -> pollingService.cancel()
            is PollingAction.ResetPolling -> pollingService.reset()
            is PollingAction.RetryPolling -> retry()
        }
    }

    private fun retry() {
        viewModelScope.launch {
            pollingService.retry()
        }
    }
}
