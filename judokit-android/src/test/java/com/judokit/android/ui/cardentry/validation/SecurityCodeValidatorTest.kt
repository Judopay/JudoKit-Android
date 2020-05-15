package com.judokit.android.ui.cardentry.validation

import com.judokit.android.R
import com.judokit.android.model.CardNetwork
import com.judokit.android.ui.cardentry.components.FormFieldEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class SecurityCodeValidatorTest {
    private val validator = SecurityCodeValidator()

    @Test
    @DisplayName("Given the card network is unknown and input length is less than 3 when field looses focus, then a validation error should be returned with check cvv string")
    fun invalidateWhenLengthLessThanThreeOnFocusChanged() {
        assertEquals(
            validator.validate("12", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.check_cvv)
        )
    }

    @Test
    @DisplayName("Given the card network is unknown and input length is less than 3 when text changes, then a validation error should be returned with empty string")
    fun invalidateWhenLengthLessThanThreeOnTextChanged() {
        assertEquals(
            validator.validate("12", FormFieldEvent.TEXT_CHANGED),
            ValidationResult(false, R.string.empty)
        )
    }

    @Test
    @DisplayName("Given the card network is AMEX and input length is less than 4 when field looses focus, then a validation error should be returned with check cvv string")
    fun invalidateAmexWhenLengthLessThanFourOnFocusChanged() {
        validator.cardNetwork = CardNetwork.AMEX

        assertEquals(
            validator.validate("123", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.check_cvv)
        )
    }

    @Test
    @DisplayName("Given the card network is AMEX and input length is less than 4 when field text changes, then a validation error should be returned with empty string")
    fun invalidateAmexWhenLengthLessThanFourOnTextChanged() {
        validator.cardNetwork = CardNetwork.AMEX

        assertEquals(
            validator.validate("123", FormFieldEvent.TEXT_CHANGED),
            ValidationResult(false, R.string.empty)
        )
    }

    @Test
    @DisplayName("Given the card network is unknown and input length is equal to 3 when field looses focus, then validation passes")
    fun validateWhenLengthEqualToThreeOnFocusChanged() {
        assertEquals(
            validator.validate("123", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(true, R.string.empty)
        )
    }

    @Test
    @DisplayName("Given the card network is unknown and input length is equal to 3 when field text changes, then validation passes")
    fun validateWhenLengthEqualToThreeOnTextChanged() {
        assertEquals(
            validator.validate("123", FormFieldEvent.TEXT_CHANGED),
            ValidationResult(true, R.string.empty)
        )
    }

    @Test
    @DisplayName("Given the card network is AMEX and input length is equal to 4 when field looses focus, then validation passes")
    fun validateAmexWhenLengthEqualToFourOnFocusChanged() {
        validator.cardNetwork = CardNetwork.AMEX

        assertEquals(
            validator.validate("1234", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(true, R.string.empty)
        )
    }

    @Test
    @DisplayName("Given the card network is AMEX and input length is equal to 4 when field text changes, then validation passes")
    fun validateAmexWhenLengthEqualToFourOnTextChanged() {
        validator.cardNetwork = CardNetwork.AMEX

        assertEquals(
            validator.validate("1234", FormFieldEvent.TEXT_CHANGED),
            ValidationResult(true, R.string.empty)
        )
    }
}
