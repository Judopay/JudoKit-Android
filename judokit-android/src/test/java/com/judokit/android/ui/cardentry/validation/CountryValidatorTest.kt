package com.judokit.android.ui.cardentry.validation

import com.judokit.android.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class CountryValidatorTest {
    private val validator: CountryValidator = CountryValidator()

    @Test
    @DisplayName("Given that no country is selected, then a validation error should be returned with please select a country string")
    fun invalidateWhenInputIsBlank() {
        assertEquals(
            validator.validate(""),
            ValidationResult(false, R.string.error_country_should_not_be_empty)
        )
    }

    @Test
    @DisplayName("Given that a country is selected, then validation passes")
    fun validateWhenInputIsNotBlank() {
        assertEquals(validator.validate("UK"), ValidationResult(true))
    }
}
