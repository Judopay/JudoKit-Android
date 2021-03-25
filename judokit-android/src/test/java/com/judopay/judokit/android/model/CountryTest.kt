package com.judopay.judokit.android.model

import com.judokit.android.R
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

    @DisplayName("Given GB.translatableName is called, then return GB from string resources")
    @Test
    fun returnUKFromStringResourcesOnTranslatableNameCall() {
        assertEquals(R.string.country_uk, Country.GB.translatableName)
    }

    @DisplayName("Given US.translatableName is called, then return US from string resources")
    @Test
    fun returnUSFromStringResourcesOnTranslatableNameCall() {
        assertEquals(R.string.country_usa, Country.US.translatableName)
    }

    @DisplayName("Given CA.translatableName is called, then return Canada from string resources")
    @Test
    fun returnCanadaFromStringResourcesOnTranslatableNameCall() {
        assertEquals(R.string.country_canada, Country.CA.translatableName)
    }

    @DisplayName("Given OTHER.translatableName is called, then return Other from string resources")
    @Test
    fun returnOtherFromStringResourcesOnTranslatableNameCall() {
        assertEquals(R.string.country_other, Country.OTHER.translatableName)
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
