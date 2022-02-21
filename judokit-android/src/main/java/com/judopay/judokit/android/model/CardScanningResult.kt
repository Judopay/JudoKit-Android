package com.judopay.judokit.android.model

import com.judopay.judokit.android.ui.cardentry.model.CardDetailsInputModel

enum class CardScanResultType {
    SUCCESS,
    CANCELLED,
    ERROR
}

data class CardScanningResult(
    val type: CardScanResultType = CardScanResultType.SUCCESS,
    val cardNumber: String = "",
    val cardHolder: String? = null,
    val expirationDate: String? = null
)

fun CardScanningResult.toInputModel() = CardDetailsInputModel(
    cardNumber = cardNumber,
    cardHolderName = cardHolder ?: "",
    expirationDate = expirationDate ?: ""
)
