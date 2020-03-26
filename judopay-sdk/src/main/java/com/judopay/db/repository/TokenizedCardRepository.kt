package com.judopay.db.repository

import androidx.lifecycle.LiveData
import com.judopay.db.dao.TokenizedCardDao
import com.judopay.db.entity.TokenizedCardEntity

class TokenizedCardRepository(private val tokenizedCardDao: TokenizedCardDao) {

    val allCardsSync: LiveData<List<TokenizedCardEntity>> =
        tokenizedCardDao.getAllSortedByIsDefaultSync()

    suspend fun findAllCards(): List<TokenizedCardEntity> = tokenizedCardDao.getAllSortedByDateAdded()

    suspend fun insert(card: TokenizedCardEntity) {
        if (card.isDefault) {
            tokenizedCardDao.updateIsDefaultToFalse()
        }
        tokenizedCardDao.insert(card)
    }

    suspend fun deleteCardWithId(id: Int) {
        tokenizedCardDao.deleteWithId(id)
    }

    suspend fun findWithId(id: Int): TokenizedCardEntity = tokenizedCardDao.getWithId(id)

    suspend fun updateLastUsedToFalse() {
        tokenizedCardDao.updateLastUsedToFalse()
    }
}
