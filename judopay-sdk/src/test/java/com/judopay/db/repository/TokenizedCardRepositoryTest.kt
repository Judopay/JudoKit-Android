package com.judopay.db.repository

import com.judopay.db.dao.TokenizedCardDao
import com.judopay.db.entity.TokenizedCardEntity
import io.mockk.coVerify
import io.mockk.every
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
    @DisplayName("getAllSortedByIsDefaultSync should return all cards ordered by isDefault descending synchronised")
    fun getAllCardsSortedByDateSynchronised() {
        verify { cardDao.getAllSortedByIsDefaultSync() }
    }

    @Test
    @DisplayName("getAllSortedByDateAdded should return all cards ordered by date ascending")
    fun getAllCardsSortedByDate() {
        runBlocking { repository.findAllCards() }

        coVerify { cardDao.getAllSortedByDateAdded() }
    }

    @Test
    @DisplayName("insert should set isDefault to false for all cards if provided card isDefault is true")
    fun shouldUpdateIsDefaultToFalse() {
        every { card.isDefault } returns true

        runBlocking { repository.insert(card) }

        coVerify { cardDao.updateIsDefaultToFalse() }
    }

    @Test
    @DisplayName("insert should not set isDefault to false for all cards when provided card isDefault is false")
    fun shouldNotUpdateIsDefaultToFalse() {
        runBlocking { repository.insert(card) }

        coVerify(exactly = 0) { cardDao.updateIsDefaultToFalse() }
    }

    @Test
    @DisplayName("updateLastUsedToFalse should set isLastUsed to false for all cards")
    fun shouldUpdateLastUsedToFalse() {
        runBlocking { repository.updateLastUsedToFalse() }

        coVerify { cardDao.updateLastUsedToFalse() }
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
