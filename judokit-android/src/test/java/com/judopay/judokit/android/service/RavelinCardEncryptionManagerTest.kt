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
        Assertions.assertTrue(sut.isCardEncryptionRequired(TransactionType.PAYMENT,true))
    }

    @DisplayName("Given Ravelin encryption is enabled, when the transaction type is Check, then return True")
    @Test
    fun returnTrueOnCheckTypeIfRavelinIsEnabled() {
        Assertions.assertTrue(sut.isCardEncryptionRequired(TransactionType.CHECK,true))
    }

    @DisplayName("Given Ravelin encryption is enabled, when the transaction type is PreAuth, then return True")
    @Test
    fun returnTrueOnPreAuthTypeIfRavelinIsEnabled() {
        Assertions.assertTrue(sut.isCardEncryptionRequired(TransactionType.PRE_AUTH,true))
    }
}
