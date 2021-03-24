package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing Country extension functions")
internal class CountryTest {

    @DisplayName("Given asCountry is called, when string is UK, then return GB")
    @Test
    fun returnGBOnAsCountryCall() {
        assertEquals(Country.GB, "UK".asCountry())
    }

    @DisplayName("Given asCountry is called, when string is USA, then return US")
    @Test
    fun returnUSOnAsCountryCall() {
        assertEquals(Country.US, "USA".asCountry())
    }

    @DisplayName("Given asCountry is called, when string is Canada, then return CA")
    @Test
    fun returnCAOnAsCountryCall() {
        assertEquals(Country.CA, "Canada".asCountry())
    }

    @DisplayName("Given asCountry is called, when string is Other, then return OTHER")
    @Test
    fun returnOtherOnAsCountryCall() {
        assertEquals(Country.OTHER, "Other".asCountry())
    }

    @DisplayName("Given postCodeMaxLength is called, when country is US, then return 5")
    @Test
    fun returnFiveOnPostCodeMaxLengthCallWithUS() {
        assertEquals(5, Country.US.postcodeMaxLength)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is CA, then return 6")
    @Test
    fun returnSixOnPostCodeMaxLengthCallWithCA() {
        assertEquals(6, Country.CA.postcodeMaxLength)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is GB, then return 8")
    @Test
    fun returnEightOnPostCodeMaxLengthCallWithGB() {
        assertEquals(8, Country.GB.postcodeMaxLength)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is OTHER, then return 8")
    @Test
    fun returnEightOnPostCodeMaxLengthCallWithOther() {
        assertEquals(8, Country.OTHER.postcodeMaxLength)
    }
}
