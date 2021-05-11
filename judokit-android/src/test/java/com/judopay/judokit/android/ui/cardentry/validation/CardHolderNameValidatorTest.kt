package com.judopay.judokit.android.ui.cardentry.validation

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
}
