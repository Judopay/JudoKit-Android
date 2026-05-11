package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing city validation")
internal class CityValidatorTest {
    private val validator = CityValidator()

    @Test
    @DisplayName("Given a valid city name is entered, then validation passes")
    fun validateValidCity() {
        assertEquals(
            validator.validate("London"),
            ValidationResult(true),
        )
    }

    @Test
    @DisplayName("Given a valid city name with spaces is entered, then validation passes")
    fun validateValidCityWithSpaces() {
        assertEquals(
            validator.validate("New York"),
            ValidationResult(true),
        )
    }

    @Test
    @DisplayName("Given a valid hyphenated city name is entered, then validation passes")
    fun validateValidHyphenatedCity() {
        assertEquals(
            validator.validate("Stratford-upon-Avon"),
            ValidationResult(true),
        )
    }

    @Test
    @DisplayName("Given a city name with digits and focus changed, then a validation error with invalid city string is returned")
    fun invalidateCityWithDigitsOnFocusChange() {
        assertEquals(
            validator.validate("City123", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_city),
        )
    }

    @Test
    @DisplayName("Given a city name with digits and no focus change, then a validation error with empty string is returned")
    fun invalidateCityWithDigits() {
        assertEquals(
            validator.validate("City123"),
            ValidationResult(false, R.string.jp_empty),
        )
    }

    @Test
    @DisplayName("Given an empty city name and focus changed, then a validation error with invalid city string is returned")
    fun invalidateEmptyCityOnFocusChange() {
        assertEquals(
            validator.validate("", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_city),
        )
    }

    @Test
    @DisplayName("Given an empty city name and no focus change, then a validation error with empty string is returned")
    fun invalidateEmptyCity() {
        assertEquals(
            validator.validate(""),
            ValidationResult(false, R.string.jp_empty),
        )
    }

    @Test
    @DisplayName("Given a city name with apostrophe, then validation passes")
    fun validateCityWithApostrophe() {
        assertEquals(
            validator.validate("King's Lynn"),
            ValidationResult(true),
        )
    }
}
