package com.judopay.judokit.android.db.repository

import com.judopay.judokit.android.db.dao.TokenizedCardDao
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
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
    @DisplayName("getAllSortedByIsDefaultSync should call tokenizedCardDao.getAllSortedByIsDefaultSync and return all cards ordered by isDefault descending synchronised")
    fun getAllCardsSortedByDateSynchronised() {
        verify { cardDao.getAllSortedByIsDefaultSync() }
    }

    @Test
    @DisplayName("insert should call tokenizedCardDao.updateAllIsDefaultToFalse when provided card isDefault = true")
    fun shouldUpdateIsDefaultToFalse() {
        every { card.isDefault } returns true

        runBlocking { repository.insert(card) }

        coVerify { cardDao.updateAllIsDefaultToFalse() }
    }

    @Test
    @DisplayName("insert should not call tokenizedCardDao.updateAllIsDefaultToFalse when provided card isDefault = false")
    fun shouldNotUpdateIsDefaultToFalse() {
        runBlocking { repository.insert(card) }

        coVerify(exactly = 0) { cardDao.updateAllIsDefaultToFalse() }
    }

    @Test
    @DisplayName("updateAllLastUsedToFalse should call tokenizedCardDao.updateAllLastUsedToFalse")
    fun shouldUpdateLastUsedToFalse() {
        runBlocking { repository.updateAllLastUsedToFalse() }

        coVerify { cardDao.updateAllLastUsedToFalse() }
    }

    @Test
    @DisplayName("insert should call tokenizedCardDao.insert with provided card")
    fun insertCardInDatabase() {
        runBlocking { repository.insert(card) }

        coVerify { cardDao.insert(card) }
    }

    @Test
    @DisplayName("deleteWithId should call tokenizedCardDao.deleteWithId with provided id")
    fun deleteCardById() {
        runBlocking { repository.deleteCardWithId(card.id) }

        coVerify { cardDao.deleteWithId(card.id) }
    }

    @Test
    @DisplayName("findWithId should call tokenizedCardDao.getWithId with provided id")
    fun findWithId() {
        runBlocking { repository.findWithId(card.id) }

        coVerify { cardDao.getWithId(card.id) }
    }
}
