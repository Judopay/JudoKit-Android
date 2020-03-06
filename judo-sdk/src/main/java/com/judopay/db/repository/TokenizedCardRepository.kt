package com.judopay.db.repository

import com.judopay.db.dao.TokenizedCardDao
import com.judopay.db.entity.TokenizedCardEntity

class TokenizedCardRepository(private val tokenizedCardDao: TokenizedCardDao) {

    suspend fun findAllCards(): List<TokenizedCardEntity> = tokenizedCardDao.getAllSortedByDateAdded()

    suspend fun insert(card: TokenizedCardEntity) {
        tokenizedCardDao.insert(card)
    }

    suspend fun deleteCardWithId(id: Int) {
        tokenizedCardDao.deleteWithId(id)
    }

    suspend fun findWithId(id: Int): TokenizedCardEntity = tokenizedCardDao.getWithId(id)

}