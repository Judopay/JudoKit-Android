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
internal class AVSCountryTest {
    @DisplayName("Given asCountry is called, when string is UK, then return GB")
    @Test
    fun returnGBOnAsCountryCall() {
        assertEquals(AVSCountry.GB, "UK".asAVSCountry())
    }

    @DisplayName("Given asCountry is called, when string is USA, then return US")
    @Test
    fun returnUSOnAsCountryCall() {
        assertEquals(AVSCountry.US, "USA".asAVSCountry())
    }

    @DisplayName("Given asCountry is called, when string is Canada, then return CA")
    @Test
    fun returnCAOnAsCountryCall() {
        assertEquals(AVSCountry.CA, "Canada".asAVSCountry())
    }

    @DisplayName("Given asCountry is called, when string is Other, then return OTHER")
    @Test
    fun returnOtherOnAsCountryCall() {
        assertEquals(AVSCountry.OTHER, "Other".asAVSCountry())
    }

    @DisplayName("Given GB.translatableName is called, then return GB from string resources")
    @Test
    fun returnUKFromStringResourcesOnTranslatableNameCall() {
        assertEquals(R.string.jp_country_uk, AVSCountry.GB.translatableName)
    }

    @DisplayName("Given US.translatableName is called, then return US from string resources")
    @Test
    fun returnUSFromStringResourcesOnTranslatableNameCall() {
        assertEquals(R.string.jp_country_usa, AVSCountry.US.translatableName)
    }

    @DisplayName("Given CA.translatableName is called, then return Canada from string resources")
    @Test
    fun returnCanadaFromStringResourcesOnTranslatableNameCall() {
        assertEquals(R.string.jp_country_canada, AVSCountry.CA.translatableName)
    }

    @DisplayName("Given OTHER.translatableName is called, then return Other from string resources")
    @Test
    fun returnOtherFromStringResourcesOnTranslatableNameCall() {
        assertEquals(R.string.jp_country_other, AVSCountry.OTHER.translatableName)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is US, then return 10")
    @Test
    fun returnFiveOnPostCodeMaxLengthCallWithUS() {
        assertEquals(POSTAL_CODE_MAX_LENGTH_USA, AVSCountry.US.postcodeMaxLength)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is CA, then return 7")
    @Test
    fun returnSixOnPostCodeMaxLengthCallWithCA() {
        assertEquals(POSTAL_CODE_MAX_LENGTH_CA, AVSCountry.CA.postcodeMaxLength)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is GB, then return 8")
    @Test
    fun returnEightOnPostCodeMaxLengthCallWithGB() {
        assertEquals(POSTAL_CODE_MAX_LENGTH_UK, AVSCountry.GB.postcodeMaxLength)
    }

    @DisplayName("Given postCodeMaxLength is called, when country is OTHER, then return 16")
    @Test
    fun returnEightOnPostCodeMaxLengthCallWithOther() {
        assertEquals(POSTAL_CODE_MAX_LENGTH_OTHER, AVSCountry.OTHER.postcodeMaxLength)
    }
}
