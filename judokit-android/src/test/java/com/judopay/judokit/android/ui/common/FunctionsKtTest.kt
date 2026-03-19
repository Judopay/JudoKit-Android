package com.judopay.judokit.android.ui.common

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing the helper functions logic")
internal class FunctionsKtTest {
    @Test
    @DisplayName("When a valid number is specified, isValidLuhnNumber() should return true")
    fun testThatWhenAValidLuhnNumberIsProvidedLuhnCheckReturnsTrue() {
        assertTrue(isValidLuhnNumber("1234567812345670"))
    }

    @Test
    @DisplayName("When a non-numeric input is specified, isValidLuhnNumber() should return false")
    fun testThatWhenANonNumericStringIsProvidedLuhnCheckReturnsFalse() {
        assertFalse(isValidLuhnNumber("asad"))
    }

    @Test
    @DisplayName("When a number that fails Luhn check is specified, isValidLuhnNumber() should return false")
    fun testThatWhenAnInvalidLuhnNumberIsProvidedLuhnCheckReturnsFalse() {
        assertFalse(isValidLuhnNumber("4111111111111112"))
    }
    
    @Test
    @DisplayName("When isDependencyPresent is called with a class that exists, it should return true")
    fun testIsDependencyPresentReturnsTrueForExistingClass() {
        assertTrue(isDependencyPresent("java.lang.String"))
    }

    @Test
    @DisplayName("When isDependencyPresent is called with a class that does not exist, it should return false")
    fun testIsDependencyPresentReturnsFalseForMissingClass() {
        assertFalse(isDependencyPresent("com.nonexistent.SomeClass"))
    }
}
