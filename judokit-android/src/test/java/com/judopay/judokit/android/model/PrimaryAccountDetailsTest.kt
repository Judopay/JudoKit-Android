package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing PrimaryAccountDetails.Builder")
internal class PrimaryAccountDetailsTest {
    @Test
    @DisplayName("Given all fields are set, then build() returns correct PrimaryAccountDetails")
    fun buildWithAllFields() {
        val details =
            PrimaryAccountDetails
                .Builder()
                .setName("John Doe")
                .setAccountNumber("1234567890")
                .setDateOfBirth("1990-01-01")
                .setPostCode("SW1A 2AA")
                .build()

        assertEquals("John Doe", details.name)
        assertEquals("1234567890", details.accountNumber)
        assertEquals("1990-01-01", details.dateOfBirth)
        assertEquals("SW1A 2AA", details.postCode)
    }

    @Test
    @DisplayName("Given no fields are set, then build() returns PrimaryAccountDetails with all null fields")
    fun buildWithNoFields() {
        val details = PrimaryAccountDetails.Builder().build()
        assertNull(details.name)
        assertNull(details.accountNumber)
        assertNull(details.dateOfBirth)
        assertNull(details.postCode)
    }

    @Test
    @DisplayName("Given only name is set, then other fields are null")
    fun buildWithOnlyName() {
        val details =
            PrimaryAccountDetails
                .Builder()
                .setName("Jane Smith")
                .build()
        assertEquals("Jane Smith", details.name)
        assertNull(details.accountNumber)
        assertNull(details.dateOfBirth)
        assertNull(details.postCode)
    }

    @Test
    @DisplayName("toString() returns expected representation")
    fun toStringReturnsExpected() {
        val details =
            PrimaryAccountDetails
                .Builder()
                .setName("John")
                .setAccountNumber("123")
                .build()
        val str = details.toString()
        assertTrue(str.contains("John"))
        assertTrue(str.contains("123"))
    }
}
