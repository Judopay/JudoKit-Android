package com.judopay.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.judopay.model.CardNetwork
import com.judopay.ui.editcard.CardPattern
import java.sql.Date
import java.util.Calendar

@Entity(tableName = "tokenized_card")
data class TokenizedCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var pattern: CardPattern = CardPattern.BLACK,
    val token: String,
    var isDefault: Boolean = false,
    var title: String,
    val expireDate: String,
    val ending: String,
    val network: CardNetwork,
    val timestamp: Date = Date(Calendar.getInstance().timeInMillis)
)
