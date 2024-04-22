package com.judopay.judokit.android.api.model.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing CardToken model logic")
internal class CardTokenTest {
    @DisplayName("Given formattedEndDate is called, when endDate is not 4 characters, then return empty string")
    @Test
    fun returnEmptyStringOnFormattedEndDateCallWithEndDateNotFourCharacters() {
        val cardToken = CardToken(endDate = "")

        assertEquals("", cardToken.formattedEndDate)
    }

    @DisplayName("Given formattedEndDate is called, when endDate is null, then return empty string")
    @Test
    fun returnEmptyStringOnFormattedEndDateCallWithEndNull() {
        val cardToken = CardToken(endDate = null)

        assertEquals("", cardToken.formattedEndDate)
    }

    @DisplayName("Given formattedEndDate is called, when endDate is 4 characters, then return formatted date string")
    @Test
    fun returnFormattedDateStringOnFormattedEndDateCallWithEndDateFourCharacters() {
        val cardToken = CardToken(endDate = "1220")

        assertEquals("12/20", cardToken.formattedEndDate)
    }
}
