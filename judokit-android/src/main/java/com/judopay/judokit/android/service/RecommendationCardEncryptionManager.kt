package com.judopay.judokit.android.service

import androidx.annotation.RequiresApi
import com.ravelin.cardEncryption.RavelinEncrypt
import com.ravelin.cardEncryption.model.CardDetails
import com.ravelin.cardEncryption.model.EncryptedCard

class RecommendationCardEncryptionManager {

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
