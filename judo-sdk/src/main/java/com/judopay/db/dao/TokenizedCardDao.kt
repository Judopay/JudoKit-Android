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
    suspend fun getAllSortedByDateAdded(): List<TokenizedCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: TokenizedCardEntity)

    @Query("DELETE FROM tokenized_card WHERE id = :id")
    suspend fun deleteWithId(id: Int)

    @Query("SELECT * FROM tokenized_card WHERE id = :id LIMIT 1")
    suspend fun getWithId(id: Int): TokenizedCardEntity
}
