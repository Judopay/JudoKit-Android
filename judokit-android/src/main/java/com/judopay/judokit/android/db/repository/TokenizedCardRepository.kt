package com.judopay.judokit.android.db.repository

import androidx.lifecycle.LiveData
import com.judopay.judokit.android.db.dao.TokenizedCardDao
import com.judopay.judokit.android.db.entity.TokenizedCardEntity

class TokenizedCardRepository(private val tokenizedCardDao: TokenizedCardDao) {
    val allCardsSync: LiveData<List<TokenizedCardEntity>> =
        tokenizedCardDao.getAllSortedByIsDefaultSync()

    suspend fun insert(card: TokenizedCardEntity) {
        if (card.isDefault) {
            tokenizedCardDao.updateAllIsDefaultToFalse()
        }
        tokenizedCardDao.insert(card)
    }

    suspend fun deleteCardWithId(id: Int) {
        tokenizedCardDao.deleteWithId(id)
    }

    suspend fun findWithId(id: Int): TokenizedCardEntity = tokenizedCardDao.getWithId(id)

    suspend fun updateAllLastUsedToFalse() {
        tokenizedCardDao.updateAllLastUsedToFalse()
    }
}
