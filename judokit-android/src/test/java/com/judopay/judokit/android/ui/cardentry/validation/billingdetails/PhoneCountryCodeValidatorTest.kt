package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing phone country code validation")
internal class PhoneCountryCodeValidatorTest {
    private val validator = PhoneCountryCodeValidator()

    @Test
    @DisplayName("Given a phone country code with exactly 4 characters, then validation passes")
    fun validateMinLengthPhoneCountryCode() {
        assertEquals(
            validator.validate("+123"),
            ValidationResult(true),
        )
    }

    @Test
    @DisplayName("Given a phone country code with more than 4 characters, then validation passes")
    fun validateLongerPhoneCountryCode() {
        assertEquals(
            validator.validate("+1234"),
            ValidationResult(true),
        )
    }

    @Test
    @DisplayName("Given a phone country code with fewer than 4 characters, then a validation error is returned")
    fun invalidateTooShortPhoneCountryCode() {
        assertEquals(
            validator.validate("+12"),
            ValidationResult(false),
        )
    }

    @Test
    @DisplayName("Given an empty phone country code, then a validation error is returned")
    fun invalidateEmptyPhoneCountryCode() {
        assertEquals(
            validator.validate(""),
            ValidationResult(false),
        )
    }

    @Test
    @DisplayName("Given a phone country code with 3 characters, then a validation error is returned")
    fun invalidateThreeCharacterPhoneCountryCode() {
        assertEquals(
            validator.validate("+44"),
            ValidationResult(false),
        )
    }

    @Test
    @DisplayName("Given a phone country code with exactly 4 characters on focus change, then validation passes")
    fun validateMinLengthPhoneCountryCodeOnFocusChange() {
        assertEquals(
            validator.validate("+123", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(true),
        )
    }
}
