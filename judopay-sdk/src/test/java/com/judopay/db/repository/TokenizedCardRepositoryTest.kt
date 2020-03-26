package com.judopay.db.repository

import com.judopay.db.dao.TokenizedCardDao
import com.judopay.db.entity.TokenizedCardEntity
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing tokenized card repository")
class TokenizedCardRepositoryTest {
    private val cardDao: TokenizedCardDao = mockk(relaxed = true)
    private val card: TokenizedCardEntity = mockk(relaxed = true)

    private val repository = TokenizedCardRepository(cardDao)

    @Test
    @DisplayName("getAllSortedByDateAddedSync should return all cards ordered by date ascending synchronised")
    fun getAllCardsSortedByDateSynchronised() {
        verify { cardDao.getAllSortedByDateAddedSync() }
    }

    @Test
    @DisplayName("getAllSortedByDateAdded should return all cards ordered by date ascending")
    fun getAllCardsSortedByDate() {
        runBlocking { repository.findAllCards() }

        coVerify { cardDao.getAllSortedByDateAdded() }
    }

    @Test
    @DisplayName("insert should add a card to the database")
    fun insertCardInDatabase() {
        runBlocking { repository.insert(card) }

        coVerify { cardDao.insert(card) }
    }

    @Test
    @DisplayName("deleteWithId should remove a card from the database by the provided id")
    fun deleteCardById() {
        runBlocking { repository.deleteCardWithId(card.id) }

        coVerify { cardDao.deleteWithId(card.id) }
    }
}
