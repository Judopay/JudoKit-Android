package com.judopay.judokit.android.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.judopay.judokit.android.db.entity.TokenizedCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenizedCardDao {
    @Query("SELECT * from tokenized_card ORDER BY isDefault DESC")
    fun getAllSortedByIsDefault(): Flow<List<TokenizedCardEntity>>

    @Query("SELECT * from tokenized_card ORDER BY timestamp ASC")
    suspend fun getAllSortedByDateAdded(): List<TokenizedCardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: TokenizedCardEntity)

    @Query("DELETE FROM tokenized_card WHERE id = :id")
    suspend fun deleteWithId(id: Int)

    @Query("SELECT * FROM tokenized_card WHERE id = :id LIMIT 1")
    suspend fun getWithId(id: Int): TokenizedCardEntity

    @Query("UPDATE tokenized_card SET isLastUsed = 0")
    suspend fun updateAllLastUsedToFalse()

    @Query("UPDATE tokenized_card SET isDefault = 0")
    suspend fun updateAllIsDefaultToFalse()
}
