package com.judopay.judokit.android.model

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing CardScanningResult")
internal class CardScanningResultKtTest {
    private val cardScanningResult = mockk<CardScanningResult> {
        every { type } returns CardScanResultType.SUCCESS
        every { cardNumber } returns "number"
        every { cardHolder } returns "holder"
        every { expirationDate } returns "expirationDate"
    }

    @DisplayName("Given toInputModel is called, then map cardNumber from CardScanningResult to cardNumber from InputModel")
    @Test
    fun mapCardNumberOnToInputModel() {
        assertEquals("number", cardScanningResult.toInputModel().cardNumber)
    }

    @DisplayName("Given toInputModel is called, then map cardHolder from CardScanningResult to cardHolder from InputModel")
    @Test
    fun mapCardHolderOnToInputModel() {
        assertEquals("holder", cardScanningResult.toInputModel().cardHolderName)
    }

    @DisplayName("Given toInputModel is called, then map expirationDate from CardScanningResult to expirationDate from InputModel")
    @Test
    fun mapExpirationDateOnToInputModel() {
        assertEquals("expirationDate", cardScanningResult.toInputModel().expirationDate)
    }

    @DisplayName("Given toInputModel is called, when expirationDate is null, then set expirationDate to empty string")
    @Test
    fun expirationDateEmptyStringWhenExpirationDateNull() {
        every { cardScanningResult.expirationDate } returns null

        assertEquals("", cardScanningResult.toInputModel().expirationDate)
    }

    @DisplayName("Given toInputModel is called, when cardHolderName is null, then set cardHolderName to empty string")
    @Test
    fun cardHolderNameEmptyStringWhenCardHolderNameNull() {
        every { cardScanningResult.cardHolder } returns null

        assertEquals("", cardScanningResult.toInputModel().cardHolderName)
    }
}
