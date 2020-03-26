package com.judopay.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.judopay.db.entity.TokenizedCardEntity

@Dao
interface TokenizedCardDao {

    @Query("SELECT * from tokenized_card ORDER BY timestamp ASC")
    fun getAllSortedByDateAddedSync(): LiveData<List<TokenizedCardEntity>>

    @Query("SELECT * from tokenized_card ORDER BY isDefault DESC")
    fun getAllSortedByIsDefaultSync(): LiveData<List<TokenizedCardEntity>>

    @Query("SELECT * from tokenized_card ORDER BY timestamp ASC")
    suspend fun getAllSortedByDateAdded(): List<TokenizedCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: TokenizedCardEntity)

    @Query("DELETE FROM tokenized_card WHERE id = :id")
    suspend fun deleteWithId(id: Int)

    @Query("SELECT * FROM tokenized_card WHERE id = :id LIMIT 1")
    suspend fun getWithId(id: Int): TokenizedCardEntity

    @Query("UPDATE tokenized_card SET isLastUsed = 0")
    suspend fun updateLastUsedToFalse()

    @Query("UPDATE tokenized_card SET isDefault = 0")
    suspend fun updateIsDefaultToFalse()
}
