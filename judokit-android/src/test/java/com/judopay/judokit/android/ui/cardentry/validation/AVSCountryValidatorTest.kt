package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CountryValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class AVSCountryValidatorTest {
    private val validator: CountryValidator = CountryValidator()

    @Test
    @DisplayName("Given that no country is selected, then a validation error should be returned with please select a country string")
    fun invalidateWhenInputIsBlank() {
        assertEquals(
            validator.validate(""),
            ValidationResult(false, R.string.jp_error_country_should_not_be_empty),
        )
    }

    @Test
    @DisplayName("Given that a country is selected, then validation passes")
    fun validateWhenInputIsNotBlank() {
        assertEquals(validator.validate("UK"), ValidationResult(true))
    }
}
