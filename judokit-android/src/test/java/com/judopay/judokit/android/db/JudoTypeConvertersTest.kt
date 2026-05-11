package com.judopay.judokit.android.db

import com.judopay.judokit.android.model.CardNetwork
import com.judopay.judokit.android.ui.editcard.CardPattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.sql.Date

@DisplayName("Test JudoTypeConverters")
internal class JudoTypeConvertersTest {
    private val sut = JudoTypeConverters()

    @Test
    @DisplayName("Given null string, then fromString returns null CardNetwork")
    fun fromStringReturnsNullForNull() {
        assertNull(sut.fromString(null))
    }

    @Test
    @DisplayName("Given valid CardNetwork name, then fromString returns the corresponding CardNetwork")
    fun fromStringReturnsCardNetworkForValidName() {
        assertEquals(CardNetwork.VISA, sut.fromString("VISA"))
    }

    @Test
    @DisplayName("Given null CardNetwork, then networkToString returns null")
    fun networkToStringReturnsNullForNull() {
        assertNull(sut.networkToString(null))
    }

    @Test
    @DisplayName("Given a CardNetwork, then networkToString returns its name")
    fun networkToStringReturnsNameForCardNetwork() {
        assertEquals("MASTERCARD", sut.networkToString(CardNetwork.MASTERCARD))
    }

    @Test
    @DisplayName("Given null string, then fromStringToCardPattern returns null")
    fun fromStringToCardPatternReturnsNullForNull() {
        assertNull(sut.fromStringToCardPattern(null))
    }

    @Test
    @DisplayName("Given valid CardPattern name, then fromStringToCardPattern returns the corresponding CardPattern")
    fun fromStringToCardPatternReturnsPatternForValidName() {
        assertEquals(CardPattern.BLACK, sut.fromStringToCardPattern("BLACK"))
    }

    @Test
    @DisplayName("Given null CardPattern, then fromCardPatternToString returns null")
    fun fromCardPatternToStringReturnsNullForNull() {
        assertNull(sut.fromCardPatternToString(null))
    }

    @Test
    @DisplayName("Given a CardPattern, then fromCardPatternToString returns its name")
    fun fromCardPatternToStringReturnsNameForPattern() {
        assertEquals("TWILIGHT_BLUE", sut.fromCardPatternToString(CardPattern.TWILIGHT_BLUE))
    }

    @Test
    @DisplayName("Given null Long, then fromTimestamp returns Date with epoch 0")
    fun fromTimestampReturnsEpochForNull() {
        assertEquals(Date(0), sut.fromTimestamp(null))
    }

    @Test
    @DisplayName("Given a Long timestamp, then fromTimestamp returns corresponding Date")
    fun fromTimestampReturnsDateForValue() {
        val timestamp = 1_000_000L
        assertEquals(Date(timestamp), sut.fromTimestamp(timestamp))
    }

    @Test
    @DisplayName("Given null Date, then dateToTimestamp returns 0")
    fun dateToTimestampReturnsZeroForNull() {
        assertEquals(0L, sut.dateToTimestamp(null))
    }

    @Test
    @DisplayName("Given a Date, then dateToTimestamp returns its time value")
    fun dateToTimestampReturnsTimeForDate() {
        val date = Date(1_000_000L)
        assertEquals(1_000_000L, sut.dateToTimestamp(date))
    }
}
