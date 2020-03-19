package com.judopay.db

import androidx.room.TypeConverter
import com.judopay.model.CardNetwork
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
    fun fromTimestamp(value: Long?): Date {
        return Date(value ?: 0)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long {
        return date?.time ?: 0
    }
}
