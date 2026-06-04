package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing CardScanningResult")
internal class CardScanningResultTest {
    @Test
    @DisplayName("Given a successful scan result, then toInputModel maps all fields")
    fun toInputModelMapsAllFields() {
        val result =
            CardScanningResult(
                type = CardScanResultType.SUCCESS,
                cardNumber = "4111111111111111",
                cardHolder = "John Doe",
                expirationDate = "12/25",
            )
        val inputModel = result.toInputModel()
        assertEquals("4111111111111111", inputModel.cardNumber)
        assertEquals("John Doe", inputModel.cardHolderName)
        assertEquals("12/25", inputModel.expirationDate)
    }

    @Test
    @DisplayName("Given null cardHolder and null expirationDate, then toInputModel maps them as empty strings")
    fun toInputModelMapsNullsAsEmptyStrings() {
        val result =
            CardScanningResult(
                type = CardScanResultType.SUCCESS,
                cardNumber = "4111111111111111",
                cardHolder = null,
                expirationDate = null,
            )
        val inputModel = result.toInputModel()
        assertEquals("", inputModel.cardHolderName)
        assertEquals("", inputModel.expirationDate)
    }

    @Test
    @DisplayName("Given default constructor values, then CardScanningResult has expected defaults")
    fun defaultConstructorValues() {
        val result = CardScanningResult()
        assertEquals(CardScanResultType.SUCCESS, result.type)
        assertEquals("", result.cardNumber)
        assertNull(result.cardHolder)
        assertNull(result.expirationDate)
    }

    @Test
    @DisplayName("Given a cancelled scan result, then type is CANCELLED")
    fun cancelledScanResultType() {
        val result = CardScanningResult(type = CardScanResultType.CANCELLED)
        assertEquals(CardScanResultType.CANCELLED, result.type)
    }

    @Test
    @DisplayName("Given an error scan result, then type is ERROR")
    fun errorScanResultType() {
        val result = CardScanningResult(type = CardScanResultType.ERROR)
        assertEquals(CardScanResultType.ERROR, result.type)
    }
}
