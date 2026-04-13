package com.judopay.judokit.android.api.model.request

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing Address.Builder")
internal class AddressTest {
    @Test
    @DisplayName("Given all fields are set, then build() returns correct Address")
    fun buildWithAllFields() {
        val address =
            Address
                .Builder()
                .setLine1("10 Downing Street")
                .setLine2("Westminster")
                .setLine3("Floor 1")
                .setTown("London")
                .setBillingCountry("GB")
                .setPostCode("SW1A 2AA")
                .setCountryCode(826)
                .setAdministrativeDivision("England")
                .build()

        assertEquals("10 Downing Street", address.line1)
        assertEquals("Westminster", address.line2)
        assertEquals("Floor 1", address.line3)
        assertEquals("London", address.town)
        assertEquals("GB", address.billingCountry)
        assertEquals("SW1A 2AA", address.postCode)
        assertEquals(826, address.countryCode)
        assertEquals("England", address.administrativeDivision)
    }

    @Test
    @DisplayName("Given no fields are set, then build() returns Address with all null fields")
    fun buildWithNoFields() {
        val address = Address.Builder().build()
        assertNull(address.line1)
        assertNull(address.line2)
        assertNull(address.line3)
        assertNull(address.town)
        assertNull(address.billingCountry)
        assertNull(address.postCode)
        assertNull(address.countryCode)
        assertNull(address.administrativeDivision)
    }

    @Test
    @DisplayName("Given blank administrativeDivision is set, then build() returns null for that field")
    fun buildWithBlankAdministrativeDivision() {
        val address =
            Address
                .Builder()
                .setAdministrativeDivision("   ")
                .build()
        assertNull(address.administrativeDivision)
    }

    @Test
    @DisplayName("Given empty administrativeDivision is set, then build() returns null for that field")
    fun buildWithEmptyAdministrativeDivision() {
        val address =
            Address
                .Builder()
                .setAdministrativeDivision("")
                .build()
        assertNull(address.administrativeDivision)
    }

    @Test
    @DisplayName("Given deprecated setState is called, then administrativeDivision is populated")
    fun setStatePopulatesAdministrativeDivision() {
        @Suppress("DEPRECATION")
        val address =
            Address
                .Builder()
                .setState("California")
                .build()
        assertEquals("California", address.administrativeDivision)
    }

    @Test
    @DisplayName("Given state getter is used, it returns same value as administrativeDivision")
    fun deprecatedStateGetterReturnsAdministrativeDivision() {
        val address =
            Address
                .Builder()
                .setAdministrativeDivision("Texas")
                .build()
        @Suppress("DEPRECATION")
        assertEquals("Texas", address.state)
    }
}
