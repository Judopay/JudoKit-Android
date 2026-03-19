package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Testing Amount class")
internal class AmountTest {
    private val sut = Amount.Builder().setAmount("1").setCurrency(Currency.GBP)

    @DisplayName("Given amount is null, then set amount to empty string")
    @Test
    fun setAmountToEmptyStringOnNull() {
        assertEquals("", sut.setAmount(null).build().amount)
    }

    @DisplayName("Given amount is empty, then set amount to empty string")
    @Test
    fun setAmountToEmptyStringOnEmpty() {
        assertEquals("", sut.setAmount("").build().amount)
    }

    @DisplayName("Given amount is not a number, then should throw exception")
    @Test
    fun throwExceptionOnAmountNotNumber() {
        assertThrows<IllegalStateException> { sut.setAmount("Not a number").build() }
    }

    @DisplayName("Given currency is null, then should throw exception")
    @Test
    fun throwExceptionOnCurrencyNull() {
        assertThrows<IllegalArgumentException> { sut.setCurrency(null).build() }
    }

    @DisplayName("Given all required fields are valid, then should not throw exception")
    @Test
    fun doesNotThrowExceptionOnAllFieldsValid() {
        assertDoesNotThrow { sut.build() }
    }

    @DisplayName("Given valid amount and GBP currency, formatted returns non-empty string")
    @Test
    fun formattedReturnsNonEmptyString() {
        val amount =
            Amount
                .Builder()
                .setAmount("10.00")
                .setCurrency(Currency.GBP)
                .build()
        val formatted = amount.formatted
        assertTrue(formatted.isNotEmpty())
    }

    @DisplayName("Given valid amount and USD currency, formatted contains amount digits")
    @Test
    fun formattedContainsAmountDigits() {
        val amount =
            Amount
                .Builder()
                .setAmount("25.99")
                .setCurrency(Currency.USD)
                .build()
        val formatted = amount.formatted
        assertTrue(formatted.contains("25") || formatted.contains("25.99"))
    }
}
