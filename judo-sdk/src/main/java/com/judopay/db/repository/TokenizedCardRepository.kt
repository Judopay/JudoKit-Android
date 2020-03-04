package com.judopay.db.repository

import androidx.lifecycle.LiveData
import com.judopay.db.dao.TokenizedCardDao
import com.judopay.db.entity.TokenizedCardEntity

class TokenizedCardRepository(private val tokenizedCardDao: TokenizedCardDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allCards: LiveData<List<TokenizedCardEntity>> = tokenizedCardDao.getAllSortedByDateAdded()

    suspend fun insert(card: TokenizedCardEntity) {
        tokenizedCardDao.insert(card)
    }

    suspend fun deleteCardWithId(id: Int) {
        tokenizedCardDao.deleteWithId(id)
    }
}