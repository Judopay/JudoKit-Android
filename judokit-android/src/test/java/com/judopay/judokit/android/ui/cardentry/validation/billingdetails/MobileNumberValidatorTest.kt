package com.judopay.judokit.android.ui.cardentry.validation.billingdetails

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.cardentry.model.FormFieldEvent
import com.judopay.judokit.android.ui.cardentry.validation.ValidationResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing mobile number validation")
internal class MobileNumberValidatorTest {
    private val validator = MobileNumberValidator()

    @Test
    @DisplayName("Given a mobile number with exactly 10 characters, then validation passes")
    fun validateMinLengthMobileNumber() {
        assertEquals(
            validator.validate("1234567890"),
            ValidationResult(true),
        )
    }

    @Test
    @DisplayName("Given a mobile number with more than 10 characters, then validation passes")
    fun validateLongMobileNumber() {
        assertEquals(
            validator.validate("+441234567890"),
            ValidationResult(true),
        )
    }

    @Test
    @DisplayName(
        "Given a mobile number with fewer than 10 characters and focus changed, validation returns an invalid number error",
    )
    fun invalidateTooShortMobileNumberOnFocusChange() {
        assertEquals(
            validator.validate("123456789", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_mobile_number),
        )
    }

    @Test
    @DisplayName(
        "Given a mobile number with fewer than 10 characters and no focus change, then a validation error with empty string is returned",
    )
    fun invalidateTooShortMobileNumber() {
        assertEquals(
            validator.validate("123456789"),
            ValidationResult(false, R.string.jp_empty),
        )
    }

    @Test
    @DisplayName("Given an empty mobile number and focus changed, then a validation error with invalid mobile number string is returned")
    fun invalidateEmptyMobileNumberOnFocusChange() {
        assertEquals(
            validator.validate("", FormFieldEvent.FOCUS_CHANGED),
            ValidationResult(false, R.string.jp_invalid_mobile_number),
        )
    }

    @Test
    @DisplayName("Given an empty mobile number and no focus change, then a validation error with empty string is returned")
    fun invalidateEmptyMobileNumber() {
        assertEquals(
            validator.validate(""),
            ValidationResult(false, R.string.jp_empty),
        )
    }
}
