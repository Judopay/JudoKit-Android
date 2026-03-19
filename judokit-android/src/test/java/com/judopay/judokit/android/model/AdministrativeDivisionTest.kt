package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing AdministrativeDivision")
internal class AdministrativeDivisionTest {
    @Test
    @DisplayName("toString() returns the name")
    fun toStringReturnsName() {
        val division = AdministrativeDivision(isoCode = "CA", name = "California")
        assertEquals("California", division.toString())
    }

    @Test
    @DisplayName("Given the american list, then it is not empty and contains expected states")
    fun americanListIsNotEmpty() {
        assertTrue(american.isNotEmpty())
        assertTrue(american.any { it.isoCode == "CA" && it.name == "California" })
        assertTrue(american.any { it.isoCode == "NY" && it.name == "New York" })
        assertTrue(american.any { it.isoCode == "TX" && it.name == "Texas" })
    }

    @Test
    @DisplayName("Given the canadian list, then it contains expected provinces")
    fun canadianListContainsProvinces() {
        assertTrue(canadian.any { it.isoCode == "ON" && it.name == "Ontario" })
        assertTrue(canadian.any { it.isoCode == "BC" && it.name == "British Columbia" })
    }

    @Test
    @DisplayName("Given the indian list, then it contains expected states")
    fun indianListContainsStates() {
        assertTrue(indian.any { it.isoCode == "GA" && it.name == "Goa" })
        assertTrue(indian.any { it.isoCode == "MH" && it.name == "Maharashtra" })
    }

    @Test
    @DisplayName("Given the chinese list, then it contains expected regions")
    fun chineseListContainsRegions() {
        assertTrue(chinese.any { it.isoCode == "BJ" && it.name == "Beijing Shi" })
    }

    @Test
    @DisplayName("Two AdministrativeDivisions with same isoCode and name are equal")
    fun equalityCheck() {
        val a = AdministrativeDivision(isoCode = "TX", name = "Texas")
        val b = AdministrativeDivision(isoCode = "TX", name = "Texas")
        assertEquals(a, b)
    }

    @Test
    @DisplayName("Two AdministrativeDivisions with different isoCode are not equal")
    fun inequalityCheck() {
        val a = AdministrativeDivision(isoCode = "TX", name = "Texas")
        val b = AdministrativeDivision(isoCode = "CA", name = "Texas")
        assertFalse(a == b)
    }
}
