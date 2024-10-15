package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.Country
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.PostcodeValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class PostcodeValidatorTest {
    private val validator = PostcodeValidator()

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is GB and postcode is invalid, then a validation error should be returned with invalid postcode entered string",
    )
    fun invalidateWrongGBPostCode() {
        validator.country = Country.GB
        assertEquals(
            validator.validate("postcode", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_postcode),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is GB and postcode is too short, then a validation error should be returned with invalid postcode entered string",
    )
    fun validateTooShortGBPostCode() {
        validator.country = Country.GB
        assertEquals(
            validator.validate("SW1Z", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_postcode),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is GB and postcode is too long, then a validation error should be returned with invalid postcode entered string",
    )
    fun validateTooLongGBPostCode() {
        validator.country = Country.GB
        assertEquals(
            validator.validate("SW1Z 1EE ", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_postcode),
        )
    }

    @Test
    @DisplayName("Given the country selected is GB and postcode is valid, then validation passes")
    fun invalidateGBPostCode() {
        validator.country = Country.GB
        assertEquals(
            validator.validate("SW15 5PU"),
            ValidationResult(true, R.string.jp_empty),
        )
    }

    @Test
    @DisplayName("Given the country selected is GB and postcode is valid on focus change, then validation passes")
    fun validateGBPostCodeOnFocusChange() {
        validator.country = Country.GB
        assertEquals(
            validator.validate("SW15 5PU", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(true, R.string.jp_empty),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is CA and postcode is invalid, then a validation error should be returned with invalid postcode entered string",
    )
    fun invalidateWrongCAPostCode() {
        validator.country = Country.CA
        assertEquals(
            validator.validate("post code", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_postcode),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is CA and postcode is too short, then a validation error should be returned with invalid postcode entered string",
    )
    fun validateTooShortCAPostCode() {
        validator.country = Country.CA
        assertEquals(
            validator.validate("A1A1A", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_postcode),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is CA and postcode is too long, then a validation error should be returned with invalid postcode entered string",
    )
    fun validateTooLongCAPostCode() {
        validator.country = Country.CA
        assertEquals(
            validator.validate("A1A 1A1 ", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_postcode),
        )
    }

    @Test
    @DisplayName("Given the country selected is CA and zipcode is valid, then validation passes")
    fun validateCAPostCode() {
        validator.country = Country.CA
        assertEquals(
            validator.validate("A1A 1A1"),
            ValidationResult(true, R.string.jp_empty),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is US and zipcode is invalid, then a validation error should be returned with invalid postcode entered string",
    )
    fun invalidateWrongUSZipCode() {
        validator.country = Country.US
        assertEquals(
            validator.validate("zipcode", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_zip_code),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is US and postcode is too short, then a validation error should be returned with invalid postcode entered string",
    )
    fun validateTooShortUSPostCode() {
        validator.country = Country.US
        assertEquals(
            validator.validate("1234", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_zip_code),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is US and postcode is too long, then a validation error should be returned with invalid postcode entered string",
    )
    fun validateTooLongUSPostCode() {
        validator.country = Country.US
        assertEquals(
            validator.validate("12345-1234 ", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_zip_code),
        )
    }

    @Test
    @DisplayName("Given the country selected is US and zipcode is valid, then validation passes")
    fun validateUSZipCode() {
        validator.country = Country.US
        assertEquals(
            validator.validate("99524"),
            ValidationResult(true, R.string.jp_empty),
        )
    }

    @Test
    @DisplayName("Given the country selected is OTHER and postcode is empty, then a validation error should be returned with empty string")
    fun invalidateBlankPostCodeForUnspecifiedCountry() {
        validator.country = Country.OTHER
        assertEquals(
            validator.validate(""),
            ValidationResult(false, R.string.jp_empty),
        )
    }

    @Test
    @DisplayName("Given the country selected is OTHER and postcode is not empty, then validation passes")
    fun validatePostCodeForUnspecifiedCountry() {
        validator.country = Country.OTHER
        assertEquals(
            validator.validate("postcode"),
            ValidationResult(true, R.string.jp_empty),
        )
    }

    @Test
    @Suppress("ktlint:standard:max-line-length", "MaxLineLength")
    @DisplayName(
        "Given the country selected is OTHER and postcode is too long, then a validation error should be returned with invalid postcode entered string",
    )
    fun validateTooLongOtherPostCode() {
        validator.country = Country.OTHER
        assertEquals(
            validator.validate("12345678901234567", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_postcode),
        )
    }

    @Test
    @DisplayName("Given the country selected is OTHER and postcode is null, then a validation error should be returned with empty string")
    fun invalidatePostCodeForNullCountry() {
        validator.country = null
        assertEquals(
            validator.validate("postcode"),
            ValidationResult(false, R.string.jp_empty),
        )
    }
}
