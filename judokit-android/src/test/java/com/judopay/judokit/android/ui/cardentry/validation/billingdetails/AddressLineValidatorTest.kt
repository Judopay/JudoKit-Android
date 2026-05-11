package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing address line validation")
internal class AddressLineValidatorTest {
    private val validator = AddressLineValidator()

    @Test
    @DisplayName("Given a valid address line is entered, then validation passes")
    fun validateValidAddressLine() {
        assertEquals(
            validator.validate("123 Main Street"),
            ValidationResult(true),
        )
    }

    @Test
    @DisplayName("Given a valid address with special characters is entered, then validation passes")
    fun validateValidAddressLineWithSpecialChars() {
        assertEquals(
            validator.validate("Flat 4, Baker Street"),
            ValidationResult(true),
        )
    }

    @Test
    @DisplayName(
        "Given an address line with unsupported characters and focus changed, validation returns an invalid address error",
    )
    fun invalidateAddressLineWithUnsupportedCharsOnFocusChange() {
        assertEquals(
            validator.validate("test@address!", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_address),
        )
    }

    @Test
    @DisplayName(
        "Given an address line with unsupported characters and no focus change, validation returns an empty error",
    )
    fun invalidateAddressLineWithUnsupportedChars() {
        assertEquals(
            validator.validate("test@address!"),
            ValidationResult(false, R.string.jp_empty),
        )
    }

    @Test
    @DisplayName("Given an empty address line and focus changed, then a validation error with invalid address string is returned")
    fun invalidateEmptyAddressOnFocusChange() {
        assertEquals(
            validator.validate("", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_address),
        )
    }

    @Test
    @DisplayName("Given an empty address line and no focus change, then a validation error with empty string is returned")
    fun invalidateEmptyAddress() {
        assertEquals(
            validator.validate(""),
            ValidationResult(false, R.string.jp_empty),
        )
    }

    @Test
    @DisplayName("Given a valid address with alphanumeric and allowed punctuation, then validation passes")
    fun validateAddressWithPunctuation() {
        assertEquals(
            validator.validate("10 Downing St/London"),
            ValidationResult(true),
        )
    }
}
