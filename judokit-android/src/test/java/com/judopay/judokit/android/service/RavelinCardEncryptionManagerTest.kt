package com.judopay.judokit.android.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing RavelinCardEncryptionManager class")
internal class RavelinCardEncryptionManagerTest {

    private val sut = RavelinCardEncryptionManager()

    @DisplayName("Given Ravelin encryption is enabled, when the transaction type is Payment, then return True")
    @Test
    fun returnTrueOnPaymentTypeIfRavelinIsEnabled() {
        Assertions.assertTrue(sut.isCardEncryptionRequired(TransactionType.PAYMENT, true))
    }

    @DisplayName("Given Ravelin encryption is enabled, when the transaction type is Check, then return True")
    @Test
    fun returnTrueOnCheckTypeIfRavelinIsEnabled() {
        Assertions.assertTrue(sut.isCardEncryptionRequired(TransactionType.CHECK, true))
    }

    @DisplayName("Given Ravelin encryption is enabled, when the transaction type is PreAuth, then return True")
    @Test
    fun returnTrueOnPreAuthTypeIfRavelinIsEnabled() {
        Assertions.assertTrue(sut.isCardEncryptionRequired(TransactionType.PRE_AUTH, true))
    }

    @DisplayName("Given cardNumber is missing, when areEncryptionArgumentsValid is called, then return False")
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

    @DisplayName("Given expirationDate is missing, when areEncryptionArgumentsValid is called, then return False")
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

    @DisplayName("Given rsaKey is missing, when areEncryptionArgumentsValid is called, then return False")
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
