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

//    @DisplayName("Given required arguments, when performCardEncryption is called, then return EncryptedCard object")
//    @Test
//    fun returnEncryptedCardOnEncryptionWhenNoArgumentIsMissing() {
//        val cardNumber = "4000 0000 0000 0002"
//        val cardHolderName = "John Smith"
//        val expirationDate = "12/25"
//        val rsaKey = "10001|C7DBB405AC4AC72D8E72882EF9ECFBC8B1B76112AE02628ABADBE6618655A53A8B53955E0E9DE33AC60E497E9B21FE207AEE7E5CEE374FC00524F0E65255AD38E1C2E8C6D336C117BBF748630E495FB2CB52120683D628F9BC8254857F4BBC68094A4A2E2AFF68CE756A3CFF60684CFC4F0C5D6C75038821C9D5D45FBFF9994569DAD08411A2788E1A7B361FBF23314105B9B0609D0DFDB092030DED91A3C3C14284984018381208BB0D635ECAFFFAE5A40B7E96013CDDE482F1D6FF95E56BF89AFBFE8C3DBD912E8048FAD6F028128788718349D11CCEACFE8718F2108E22D07B927286676C61F4BB5A65151D28F56687C588A3A0193395E31DABA549BD160D"
//
//        val encryptedCard = sut.performCardEncryption(
//            cardNumber, cardHolderName, expirationDate, rsaKey
//        )
//
//        Assertions.assertNotNull(encryptedCard)
//        Assertions.assertNotNull(encryptedCard?.cardCiphertext)
//        Assertions.assertNotNull(encryptedCard?.aesKeyCiphertext)
//        Assertions.assertNotNull(encryptedCard?.ravelinSDKVersion)
//        Assertions.assertNotNull(encryptedCard?.algorithm)
//        Assertions.assertNotNull(encryptedCard?.keyIndex)
//    }
}
