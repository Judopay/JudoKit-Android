package com.judokit.android.api.model.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

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
}
