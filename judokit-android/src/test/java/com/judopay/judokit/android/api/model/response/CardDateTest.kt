package com.judopay.judokit.android.api.model.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Locale

internal class CardDateTest {
    @Test
    @DisplayName("When a MM/yy date format is specified, splitDate(date: String) should return pair of integers")
    fun testThatSplitDateReturnsPairOfIntegers() {
        assertEquals(CardDate("12/20").month, 12)
        assertEquals(CardDate("12/20").year, 2020)
    }

    @Test
    @DisplayName("When a random string is specified, splitDate(date: String) should return pair of zeros")
    fun testThatSplitDateReturnsPairOfZeros() {
        assertEquals(CardDate("random string").month, 0)
        assertEquals(CardDate("random string").year, 0)
    }

    @Test
    @DisplayName("No-slash MMyyformat is parsed correctly")
    fun noSlashFormatParsedCorrectly() {
        val cardDate = CardDate("1220")
        assertEquals(12, cardDate.month)
        assertEquals(2020, cardDate.year)
    }

    @Test
    @DisplayName("Month greater than 12 (e.g. 13/20) results in zeros")
    fun invalidMonthAbove12ResultsInZeros() {
        val cardDate = CardDate("13/20")
        assertEquals(0, cardDate.month)
        assertEquals(0, cardDate.year)
    }

    @Test
    @DisplayName("Month 00 (e.g. 00/20) results in zeros")
    fun invalidMonthZeroResultsInZeros() {
        val cardDate = CardDate("00/20")
        assertEquals(0, cardDate.month)
        assertEquals(0, cardDate.year)
    }

    @Test
    @DisplayName("Empty string results in zeros")
    fun emptyStringResultsInZeros() {
        val cardDate = CardDate("")
        assertEquals(0, cardDate.month)
        assertEquals(0, cardDate.year)
    }

    @Test
    @DisplayName("Setting date via property triggers re-parsing")
    fun settingDatePropertyReparses() {
        val cardDate = CardDate("12/20")
        assertEquals(12, cardDate.month)
        cardDate.date = "06/25"
        assertEquals(6, cardDate.month)
        assertEquals(2025, cardDate.year)
    }

    @Test
    @DisplayName("Setting date property to invalid value resets month and year to zero")
    fun settingDatePropertyToInvalidValueResetsToZeros() {
        val cardDate = CardDate("12/25")
        cardDate.date = "invalid"
        assertEquals(0, cardDate.month)
        assertEquals(0, cardDate.year)
    }

    @Test
    @DisplayName("isAfterToday returns true for a date 2 years in the future")
    fun isAfterTodayTrueForFutureDate() {
        val future = Calendar.getInstance()
        future.add(Calendar.YEAR, 2)
        val month = String.format(Locale.ROOT, "%02d", future.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", future.get(Calendar.YEAR) % 100)
        assertTrue(CardDate("$month/$year").isAfterToday)
    }

    @Test
    @DisplayName("isAfterToday returns false for a date 5 years in the past")
    fun isAfterTodayFalseForPastDate() {
        assertFalse(CardDate("01/10").isAfterToday)
    }

    @Test
    @DisplayName("isAfterToday returns false for invalid date (zeros)")
    fun isAfterTodayFalseForInvalidDate() {
        assertFalse(CardDate("").isAfterToday)
    }

    @Test
    @DisplayName("isAfterToday returns true for current month (card valid until end of month)")
    fun isAfterTodayTrueForCurrentMonth() {
        val now = Calendar.getInstance()
        val lastDay = now.getActualMaximum(Calendar.DAY_OF_MONTH)
        Assumptions.assumeTrue(now.get(Calendar.DAY_OF_MONTH) < lastDay, "Skipped on last day of month")
        val month = String.format(Locale.ROOT, "%02d", now.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", now.get(Calendar.YEAR) % 100)
        assertTrue(CardDate("$month/$year").isAfterToday)
    }

    @Test
    @DisplayName("isBeforeToday returns true for a date 5 years in the past")
    fun isBeforeTodayTrueForPastDate() {
        assertTrue(CardDate("01/10").isBeforeToday)
    }

    @Test
    @DisplayName("isBeforeToday returns true for a date 1 month in the past")
    fun isBeforeTodayTrueForOneMonthAgo() {
        val past = Calendar.getInstance()
        past.add(Calendar.MONTH, -1)
        val month = String.format(Locale.ROOT, "%02d", past.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", past.get(Calendar.YEAR) % 100)
        assertTrue(CardDate("$month/$year").isBeforeToday)
    }

    @Test
    @DisplayName("isBeforeToday returns false for a date 2 years in the future")
    fun isBeforeTodayFalseForFutureDate() {
        val future = Calendar.getInstance()
        future.add(Calendar.YEAR, 2)
        val month = String.format(Locale.ROOT, "%02d", future.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", future.get(Calendar.YEAR) % 100)
        assertFalse(CardDate("$month/$year").isBeforeToday)
    }

    @Test
    @DisplayName("isBeforeToday returns false for invalid date (zeros)")
    fun isBeforeTodayFalseForInvalidDate() {
        assertFalse(CardDate("").isBeforeToday)
    }

    @Test
    @DisplayName("isInsideAllowedDateRange returns true for a date within 10 years from now")
    fun isInsideAllowedDateRangeTrueForDateWithinRange() {
        val future = Calendar.getInstance()
        future.add(Calendar.YEAR, 5)
        val month = String.format(Locale.ROOT, "%02d", future.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", future.get(Calendar.YEAR) % 100)
        assertTrue(CardDate("$month/$year").isInsideAllowedDateRange)
    }

    @Test
    @DisplayName("isInsideAllowedDateRange returns false for a date 15 years in the past")
    fun isInsideAllowedDateRangeFalseForDateTooFarInPast() {
        assertFalse(CardDate("01/05").isInsideAllowedDateRange)
    }

    @Test
    @DisplayName("isInsideAllowedDateRange returns false for a date 15 years in the future")
    fun isInsideAllowedDateRangeFalseForDateTooFarInFuture() {
        val farFuture = Calendar.getInstance()
        farFuture.add(Calendar.YEAR, 15)
        val month = String.format(Locale.ROOT, "%02d", farFuture.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", farFuture.get(Calendar.YEAR) % 100)
        assertFalse(CardDate("$month/$year").isInsideAllowedDateRange)
    }

    @Test
    @DisplayName("isInsideAllowedDateRange returns false for invalid date (zeros)")
    fun isInsideAllowedDateRangeFalseForInvalidDate() {
        assertFalse(CardDate("").isInsideAllowedDateRange)
    }

    @Test
    @DisplayName("isExpiredInTwoMonths returns true for a date one month from now")
    fun isExpiredInTwoMonthsTrueForDateOneMonthAway() {
        val soon = Calendar.getInstance()
        soon.add(Calendar.MONTH, 1)
        val month = String.format(Locale.ROOT, "%02d", soon.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", soon.get(Calendar.YEAR) % 100)
        assertTrue(CardDate("$month/$year").isExpiredInTwoMonths)
    }

    @Test
    @DisplayName("isExpiredInTwoMonths returns false for a date 2 years from now")
    fun isExpiredInTwoMonthsFalseForDateFarInFuture() {
        val far = Calendar.getInstance()
        far.add(Calendar.YEAR, 2)
        val month = String.format(Locale.ROOT, "%02d", far.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", far.get(Calendar.YEAR) % 100)
        assertFalse(CardDate("$month/$year").isExpiredInTwoMonths)
    }

    @Test
    @DisplayName("isExpiredInTwoMonths returns false for a card expiring at exactly two months (exclusive upper boundary)")
    fun isExpiredInTwoMonthsFalseForExactTwoMonthBoundary() {
        val boundary = Calendar.getInstance()
        boundary.add(Calendar.MONTH, 2)
        val month = String.format(Locale.ROOT, "%02d", boundary.get(Calendar.MONTH) + 1)
        val year = String.format(Locale.ROOT, "%02d", boundary.get(Calendar.YEAR) % 100)
        assertFalse(CardDate("$month/$year").isExpiredInTwoMonths)
    }

    @Test
    @DisplayName("isExpiredInTwoMonths returns false for an already-expired date")
    fun isExpiredInTwoMonthsFalseForAlreadyExpiredDate() {
        assertFalse(CardDate("01/10").isExpiredInTwoMonths)
    }

    @Test
    @DisplayName("isExpiredInTwoMonths returns false for invalid date (zeros)")
    fun isExpiredInTwoMonthsFalseForInvalidDate() {
        assertFalse(CardDate("").isExpiredInTwoMonths)
    }
}
