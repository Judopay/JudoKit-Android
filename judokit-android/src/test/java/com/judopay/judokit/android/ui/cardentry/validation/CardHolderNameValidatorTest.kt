package com.judopay.judokit.android.ui.cardentry.validation

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.carddetails.CardHolderNameValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class CardHolderNameValidatorTest {
    private val validator = CardHolderNameValidator()

    @Test
    @DisplayName("Given that input length is greater than 4, then validation passes")
    fun validateWhenTextLengthGreaterThanThree() {
        assertEquals(validator.validate("Name"), ValidationResult(true))
    }

    @Test
    @DisplayName("Given that input length is less or equal to three, then a validation error should be returned with empty string")
    fun invalidateWhenTextLengthLessOrEqualThanThree() {
        assertEquals(validator.validate("Nam"), ValidationResult(false))
    }

    @Test
    @DisplayName("ValidationResult toString contains field values")
    fun validationResultToString() {
        val result = ValidationResult(isValid = true, message = R.string.jp_empty)
        val str = result.toString()
        assertEquals("ValidationResult(isValid=true, message=${R.string.jp_empty})", str)
    }

    @Test
    @DisplayName("Given blank input with FOCUS_CHANGED, then message is jp_card_holder_name_required")
    fun blankInputWithFocusChangedReturnsRequiredMessage() {
        val result = validator.validate("", FormFieldEvent.FOCUS_CHANGED)
        assertEquals(ValidationResult(false, R.string.jp_card_holder_name_required), result)
    }

    @Test
    @DisplayName("Given too-short input with FOCUS_CHANGED, then message is jp_card_holder_name_too_short")
    fun tooShortInputWithFocusChangedReturnsTooShortMessage() {
        val result = validator.validate("Nam", FormFieldEvent.FOCUS_CHANGED)
        assertEquals(ValidationResult(false, R.string.jp_card_holder_name_too_short), result)
    }

    @Test
    @DisplayName("Given input with invalid characters and FOCUS_CHANGED, then message is jp_card_holder_name_special_chars")
    fun invalidCharsWithFocusChangedReturnsSpecialCharsMessage() {
        val result = validator.validate("Ab!!", FormFieldEvent.FOCUS_CHANGED)
        assertEquals(ValidationResult(false, R.string.jp_card_holder_name_special_chars), result)
    }

    @Test
    @DisplayName("Given valid input with FOCUS_CHANGED, then validation passes with empty message")
    fun validInputWithFocusChangedPassesValidation() {
        val result = validator.validate("Alice", FormFieldEvent.FOCUS_CHANGED)
        assertEquals(ValidationResult(true, R.string.jp_empty), result)
    }
}
