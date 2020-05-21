package com.judokit.android.ui.cardentry.validation

import com.judokit.android.R
import com.judokit.android.api.model.response.CardDate
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing expiration date validation")
internal class ExpirationDateValidatorTest {
    private val cardDate: CardDate = mockk(relaxed = true)

    private val sut = ExpirationDateValidator(cardDate = cardDate)

    @DisplayName("Given date is before today, then return isValid false")
    @Test
    fun returnIsValidFalseWhenDateBeforeToday() {
        every { cardDate.isAfterToday } returns false
        every { cardDate.isInsideAllowedDateRange } returns true

        assertEquals(ValidationResult(false, R.string.check_expiry_date), sut.validate("12/20"))
    }

    @DisplayName("Given date is not inside allowed date range, then return isValid false")
    @Test
    fun returnIsValidFalseWhenDateIsNotInsideAllowedDateRange() {
        every { cardDate.isAfterToday } returns true
        every { cardDate.isInsideAllowedDateRange } returns false

        assertEquals(ValidationResult(false, R.string.check_expiry_date), sut.validate("12/20"))
    }

    @DisplayName("Given date is valid, then return isValid true")
    @Test
    fun returnIsValidTrueWhenDateIsValid() {
        every { cardDate.isAfterToday } returns true
        every { cardDate.isInsideAllowedDateRange } returns true

        assertEquals(ValidationResult(true), sut.validate("12/20"))
    }

    @DisplayName("Given date is not five characters, then return empty string string")
    @Test
    fun returnCheckExpiryDateStringWhenDateNotFiveCharacters() {
        every { cardDate.isAfterToday } returns false
        every { cardDate.isInsideAllowedDateRange } returns false

        assertEquals(R.string.empty, sut.validate("12/2").message)
    }
}
