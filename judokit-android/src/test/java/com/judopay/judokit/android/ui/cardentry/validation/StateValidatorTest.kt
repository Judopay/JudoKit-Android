package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.Country
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.StateValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class StateValidatorTest {
    private val validator: StateValidator = StateValidator()

    @Test
    @DisplayName("Given that no state is selected for the US, then a validation error should be returned with the correct message")
    fun invalidWhenInputIsBlankForUS() {
        validator.country = Country.US
        assertEquals(
            validator.validate(""),
            ValidationResult(false, R.string.error_state_should_not_be_empty)
        )
    }

    @Test
    @DisplayName("Given that no province/territory is selected for Canada, then validation error should be returned with the correct message")
    fun invalidWhenInputIsBlankForCA() {
        validator.country = Country.CA
        assertEquals(
            validator.validate(""),
            ValidationResult(false, R.string.error_province_territory_should_not_be_empty)
        )
    }

    @Test
    @DisplayName("Given that a state is selected for a country that has no states, then validation passes")
    fun validWhenInputIsNotBlankForGB() {
        validator.country = Country.GB
        assertEquals(validator.validate("Whatever state"), ValidationResult(true))
    }

    @Test
    @DisplayName("Given that a state is not selected for a country that has no states, then validation passes")
    fun validWhenInputIsBlankForOtherCountry() {
        validator.country = Country.OTHER
        assertEquals(validator.validate(""), ValidationResult(true))
    }
}
