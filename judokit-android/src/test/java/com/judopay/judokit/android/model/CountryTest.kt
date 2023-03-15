package com.judopay.judokit.android.model

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_CA
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_OTHER
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_UK
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_USA
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

    @DisplayName("Given postCodeMaxLength is called, when country is US, then return 10")
    @Test
    fun returnFiveOnPostCodeMaxLengthCallWithUS() {
        assertEquals(POSTAL_CODE_MAX_LENGTH_USA, Country.US.postcodeMaxLength)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is CA, then return 7")
    @Test
    fun returnSixOnPostCodeMaxLengthCallWithCA() {
        assertEquals(POSTAL_CODE_MAX_LENGTH_CA, Country.CA.postcodeMaxLength)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is GB, then return 8")
    @Test
    fun returnEightOnPostCodeMaxLengthCallWithGB() {
        assertEquals(POSTAL_CODE_MAX_LENGTH_UK, Country.GB.postcodeMaxLength)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is OTHER, then return 16")
    @Test
    fun returnEightOnPostCodeMaxLengthCallWithOther() {
        assertEquals(POSTAL_CODE_MAX_LENGTH_OTHER, Country.OTHER.postcodeMaxLength)
    }
}
