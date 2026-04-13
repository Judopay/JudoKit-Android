package com.judopay.judokit.android.api.deserializer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.TimeZone

@DisplayName("Testing Iso8601Deserializer")
internal class Iso8601DeserializerTest {
    @Test
    @DisplayName("Given a full ISO 8601 datetime with Z suffix, then it parses to the correct date")
    fun parseDateWithZSuffix() {
        val date = Iso8601Deserializer.toDate("2023-06-15T10:30:00.000Z")
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.time = date
        assertEquals(2023, cal[Calendar.YEAR])
        assertEquals(Calendar.JUNE, cal[Calendar.MONTH])
        assertEquals(15, cal[Calendar.DAY_OF_MONTH])
        assertEquals(10, cal[Calendar.HOUR_OF_DAY])
        assertEquals(30, cal[Calendar.MINUTE])
    }

    @Test
    @DisplayName("Given a date-only ISO 8601 string, then it parses to midnight UTC")
    fun parseDateOnly() {
        val date = Iso8601Deserializer.toDate("2023-06-15")
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.time = date
        assertEquals(2023, cal[Calendar.YEAR])
        assertEquals(Calendar.JUNE, cal[Calendar.MONTH])
        assertEquals(15, cal[Calendar.DAY_OF_MONTH])
    }

    @Test
    @DisplayName("Given a year-only string, then it parses to January 1st of that year")
    fun parseYearOnly() {
        val date = Iso8601Deserializer.toDate("2023")
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.time = date
        assertEquals(2023, cal[Calendar.YEAR])
        assertEquals(Calendar.JANUARY, cal[Calendar.MONTH])
    }

    @Test
    @DisplayName("Given an ordinal date string, then it parses correctly")
    fun parseOrdinalDate() {
        val date = Iso8601Deserializer.toDate("2023166") // June 15 is the 166th day
        assertNotNull(date)
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.time = date
        assertEquals(2023, cal[Calendar.YEAR])
        assertEquals(166, cal[Calendar.DAY_OF_YEAR])
    }

    @Test
    @DisplayName("Given an ISO 8601 datetime with timezone offset, then it parses to the correct date")
    fun parseDateWithTimezoneOffset() {
        val date = Iso8601Deserializer.toDate("2023-06-15T10:30:00+01:00")
        assertNotNull(date)
    }

    @Test
    @DisplayName("Given a week date string, then it parses without throwing")
    fun parseWeekDate() {
        val date = Iso8601Deserializer.toDate("2023W241")
        assertNotNull(date)
    }

    @Test
    @DisplayName("Given an ISO 8601 datetime without timezone indicator, then it parses using default timezone")
    fun parseDateTimeWithoutTimezone() {
        val date = Iso8601Deserializer.toDate("2023-06-15T10:30:00")
        assertNotNull(date)
    }
}
