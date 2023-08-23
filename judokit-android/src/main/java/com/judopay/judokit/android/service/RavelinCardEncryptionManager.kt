package com.judopay.judokit.android.service

import android.util.Log
import androidx.annotation.RequiresApi
import com.ravelin.cardEncryption.RavelinEncrypt
import com.ravelin.cardEncryption.model.CardDetails
import com.ravelin.cardEncryption.model.EncryptedCard

class RavelinCardEncryptionManager {

    fun isCardEncryptionRequired(
        type: TransactionType,
        isRavelinEncryptionEnabled: Boolean
    ) = isRavelinEncryptionEnabled && (
        type == TransactionType.PAYMENT ||
            type == TransactionType.CHECK ||
            type == TransactionType.PRE_AUTH
        )

    fun areEncryptionArgumentsValid(
        cardNumber: String?,
        expirationDate: String?,
        rsaKey: String?
    ): Boolean {
        // Todo: Confirm with Stefan that form of logging is correct.
        if (cardNumber.isNullOrEmpty()) {
            Log.e(
                CardTransactionManager::class.java.name,
                "Encryption arguments validation: Card number is required."
            )
            return false
        }
        if (expirationDate.isNullOrEmpty()) {
            Log.e(
                CardTransactionManager::class.java.name,
                "Encryption arguments validation: Expiration date is required."
            )
            return false
        }
        if (expirationDate.length != 5) {
            Log.e(
                CardTransactionManager::class.java.name,
                "Encryption arguments validation: Expiration date length is not correct."
            )
            return false
        }
        if (rsaKey.isNullOrEmpty()) {
            Log.e(
                CardTransactionManager::class.java.name,
                "Encryption arguments validation: The RSAPublicKey field in the ravelin recommendation configuration is required."
            )
            return false
        }
        return true
    }

    @RequiresApi(22)
    fun performCardEncryption(
        cardNumber: String,
        cardHolderName: String?,
        expirationDate: String,
        rsaKey: String
    ): EncryptedCard? {
        val expiryMonth = expirationDate.substring(0, 2)
        val expiryYear = expirationDate.substring(3, 5)
        val cardDetails = CardDetails(cardNumber, expiryMonth, expiryYear, cardHolderName)
        return RavelinEncrypt().encryptCard(cardDetails, rsaKey)
    }
}
