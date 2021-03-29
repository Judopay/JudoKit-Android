package com.judopay.judokit.android.db

import androidx.room.TypeConverter
import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.ui.editcard.CardPattern
import java.sql.Date

class JudoTypeConverters {

    @TypeConverter
    fun fromString(value: String?): CardNetwork? {
        return if (value != null) CardNetwork.valueOf(value) else null
    }

    @TypeConverter
    fun networkToString(network: CardNetwork?): String? {
        return network?.name
    }

    @TypeConverter
    fun fromStringToCardPattern(value: String?): CardPattern? {
        return if (value != null) CardPattern.valueOf(value) else null
    }

    @TypeConverter
    fun fromCardPatternToString(pattern: CardPattern?): String? {
        return pattern?.name
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date {
        return Date(value ?: 0)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long {
        return date?.time ?: 0
    }
}
