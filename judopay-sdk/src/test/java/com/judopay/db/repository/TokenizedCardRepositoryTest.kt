package com.judopay.db.repository

import com.judopay.db.dao.TokenizedCardDao
import com.judopay.db.entity.TokenizedCardEntity
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TokenizedCardRepositoryTest {
    val cardDao: TokenizedCardDao = mockk(relaxed = true)
    val card: TokenizedCardEntity = mockk(relaxed = true)

    val repository = TokenizedCardRepository(cardDao)

    @Test
    fun `get all cards sorted by date synchronised`() {
        verify { cardDao.getAllSortedByDateAddedSync() }
    }

    @Test
    fun `get all cards sorted by date`() {
        runBlocking { repository.findAllCards() }

        coVerify { cardDao.getAllSortedByDateAdded() }
    }

    @Test
    fun `insert card in database`() {
        runBlocking { repository.insert(card) }

        coVerify { cardDao.insert(card) }
    }

    @Test
    fun `delete card by id`() {
        runBlocking { repository.deleteCardWithId(card.id) }

        coVerify { cardDao.deleteWithId(card.id) }
    }
}