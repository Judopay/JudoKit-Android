package com.judokit.android.ui.cardentry.validation

import com.judokit.android.R
import com.judokit.android.model.Country
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class PostcodeValidatorTest {
    private val validator = PostcodeValidator()

    @Test
    @DisplayName("Given the country selected is GB and postcode is invalid, then a validation error should be returned with invalid postcode entered string")
    fun invalidateWrongGBPostCode() {
        validator.country = Country.GB
        assertEquals(
            validator.validate("postcode"),
            ValidationResult(false, R.string.invalid_postcode)
        )
    }

    @Test
    @DisplayName("Given the country selected is GB and postcode is valid, then validation passes")
    fun invalidateGBPostCode() {
        validator.country = Country.GB
        assertEquals(
            validator.validate("SW15 5PU"),
            ValidationResult(true, R.string.empty)
        )
    }

    @Test
    @DisplayName("Given the country selected is CA and postcode is invalid, then a validation error should be returned with invalid postcode entered string")
    fun invalidateWrongCAPostCode() {
        validator.country = Country.CA
        assertEquals(
            validator.validate("post code"),
            ValidationResult(false, R.string.invalid_postcode)
        )
    }

    /**
     * TODO: Fix postcode validation
     @Test
     @DisplayName("validate post code for CA")
     fun validateCAPostCode() {
     validator.country = Country.CA
     assertEquals(
     validator.validate("A1A 1A1"),
     ValidationResult(true, R.string.empty)
     )
     }
     */

    @Test
    @DisplayName("Given the country selected is US and zipcode is invalid, then a validation error should be returned with invalid postcode entered string")
    fun invalidateWrongUSZipCode() {
        validator.country = Country.US
        assertEquals(
            validator.validate("zipcode"),
            ValidationResult(false, R.string.invalid_zip_code)
        )
    }

    @Test
    @DisplayName("Given the country selected is US and zipcode is valid, then validation passes")
    fun validateUSZipCode() {
        validator.country = Country.US
        assertEquals(
            validator.validate("99524"),
            ValidationResult(true, R.string.empty)
        )
    }

    @Test
    @DisplayName("Given the country selected is OTHER and postcode is empty, then a validation error should be returned with empty string")
    fun invalidateBlankPostCodeForUnspecifiedCountry() {
        validator.country = Country.OTHER
        assertEquals(
            validator.validate(""),
            ValidationResult(false, R.string.empty)
        )
    }

    @Test
    @DisplayName("Given the country selected is OTHER and postcode is not empty, then validation passes")
    fun validatePostCodeForUnspecifiedCountry() {
        validator.country = Country.OTHER
        assertEquals(
            validator.validate("postcode"),
            ValidationResult(true, R.string.empty)
        )
    }

    @Test
    @DisplayName("Given the country selected is OTHER and postcode is null, then a validation error should be returned with empty string")
    fun invalidatePostCodeForNullCountry() {
        validator.country = null
        assertEquals(
            validator.validate("postcode"),
            ValidationResult(false, R.string.empty)
        )
    }
}
