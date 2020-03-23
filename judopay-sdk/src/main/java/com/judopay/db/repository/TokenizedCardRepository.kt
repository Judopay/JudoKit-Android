package com.judopay.db.repository

import androidx.lifecycle.LiveData
import com.judopay.db.dao.TokenizedCardDao
import com.judopay.db.entity.TokenizedCardEntity

class TokenizedCardRepository(private val tokenizedCardDao: TokenizedCardDao) {

    val allCardsSync: LiveData<List<TokenizedCardEntity>> = tokenizedCardDao.getAllSortedByDateAddedSync()

    suspend fun findAllCards(): List<TokenizedCardEntity> = tokenizedCardDao.getAllSortedByDateAdded()

    suspend fun insert(card: TokenizedCardEntity) {
        tokenizedCardDao.insert(card)
    }

    suspend fun updateDefault(card: TokenizedCardEntity) {
        if (card.isDefault) {
            findAllCards().forEach {
                if (it.isDefault) tokenizedCardDao.insert(it.apply { isDefault = false })
            }
        }
        tokenizedCardDao.insert(card)
    }

    suspend fun deleteCardWithId(id: Int) {
        tokenizedCardDao.deleteWithId(id)
    }

    suspend fun findWithId(id: Int): TokenizedCardEntity = tokenizedCardDao.getWithId(id)

    suspend fun updateLastUsed(){
        tokenizedCardDao.updateLastUsed()
    }
}
