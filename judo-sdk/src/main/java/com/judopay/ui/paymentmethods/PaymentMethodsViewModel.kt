package com.judopay.ui.paymentmethods

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.judopay.persistence.JudoRoomDatabase
import com.judopay.persistence.entity.TokenizedCardEntity
import com.judopay.persistence.repository.TokenizedCardRepository
import kotlinx.coroutines.launch

class PaymentMethodsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TokenizedCardRepository
    val allCards: LiveData<List<TokenizedCardEntity>>

    init {
        val tokenizedCardDao = JudoRoomDatabase.getDatabase(application).tokenizedCardDao()
        repository = TokenizedCardRepository(tokenizedCardDao)
        allCards = repository.allCards
    }

    fun insert(card: TokenizedCardEntity) = viewModelScope.launch {
        repository.insert(card)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}
