package com.judokit.android.ui.common

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing the helper functions logic")
internal class FunctionsKtTest {

    @Test
    @DisplayName("When a valid number is specified, isValidLuhnNumber() should return true")
    fun testThatWhenAValidLuhnNumberIsProvidedLuhnCheckReturnsTrue() {
        assert(isValidLuhnNumber("1234567812345670"))
    }

    @Test
    @DisplayName("When a non-numeric input is specified, isValidLuhnNumber() should return false")
    fun testThatWhenANonNumericStringIsProvidedLuhnCheckReturnsFalse() {
        assertFalse(isValidLuhnNumber("asad"))
    }
}
