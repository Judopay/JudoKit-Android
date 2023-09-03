package com.judopay.judokit.android.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing RecommendationCardEncryptionManagerTest class")
internal class RecommendationCardEncryptionManagerTest {

    private val sut = RecommendationCardEncryptionManager()

    @DisplayName("Given Recommendation Feature is enabled, when the transaction type is Payment, then return true")
    @Test
    fun returnTrueOnPaymentTypeIfRecommendationIsEnabled() {
        Assertions.assertTrue(sut.isCardEncryptionRequired(TransactionType.PAYMENT, true))
    }

    @DisplayName("Given Recommendation Feature is enabled, when the transaction type is Check, then return true")
    @Test
    fun returnTrueOnCheckTypeIfRecommendationIsEnabled() {
        Assertions.assertTrue(sut.isCardEncryptionRequired(TransactionType.CHECK, true))
    }

    @DisplayName("Given Recommendation Feature is enabled, when the transaction type is PreAuth, then return true")
    @Test
    fun returnTrueOnPreAuthTypeIfRecommendationIsEnabled() {
        Assertions.assertTrue(sut.isCardEncryptionRequired(TransactionType.PRE_AUTH, true))
    }

    @DisplayName("Given cardNumber is missing, when areEncryptionArgumentsValid is called, then return false")
    @Test
    fun returnFalseOnEncryptionArgumentsValidationIfCardNumberIsMissing() {
        val cardNumber = null
        val expirationDate = "12/24"
        val rsaKey = "123456778910ABCDE"
        Assertions.assertFalse(
            sut.areEncryptionArgumentsValid(
                cardNumber,
                expirationDate,
                rsaKey
            )
        )
    }

    @DisplayName("Given expirationDate is missing, when areEncryptionArgumentsValid is called, then return false")
    @Test
    fun returnFalseOnEncryptionArgumentsValidationIfExpirationDateIsMissing() {
        val cardNumber = "4000 0000 0000 0002"
        val expirationDate = null
        val rsaKey = "123456778910ABCDE"
        Assertions.assertFalse(
            sut.areEncryptionArgumentsValid(
                cardNumber,
                expirationDate,
                rsaKey
            )
        )
    }

    @DisplayName("Given expirationDate's length is incorrect, when areEncryptionArgumentsValid is called, then return false")
    @Test
    fun returnFalseOnEncryptionArgumentsValidationIfExpirationDateHasIncorrectLength() {
        val cardNumber = "4000 0000 0000 0002"
        val expirationDate = "10/20/2020"
        val rsaKey = "123456778910ABCDE"
        Assertions.assertFalse(
            sut.areEncryptionArgumentsValid(
                cardNumber,
                expirationDate,
                rsaKey
            )
        )
    }

    @DisplayName("Given rsaKey is missing, when areEncryptionArgumentsValid is called, then return false")
    @Test
    fun returnFalseOnEncryptionArgumentsValidationIfRsaKeyIsMissing() {
        val cardNumber = "4000 0000 0000 0002"
        val expirationDate = "12/24"
        val rsaKey = null
        Assertions.assertFalse(
            sut.areEncryptionArgumentsValid(cardNumber, expirationDate, rsaKey)
        )
    }
}
