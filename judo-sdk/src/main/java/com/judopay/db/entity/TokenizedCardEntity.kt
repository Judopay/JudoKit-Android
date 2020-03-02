package com.judopay.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.judopay.model.CardNetwork
import java.sql.Date
import java.util.*

@Entity(tableName = "tokenized_card")
data class TokenizedCardEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val token: String,
        val isDefault: Boolean = false,
        val title: String,
        val expireDate: String,
        val ending: String,
        val network: CardNetwork,
        val timestamp: Date = Date(Calendar.getInstance().timeInMillis)
)


