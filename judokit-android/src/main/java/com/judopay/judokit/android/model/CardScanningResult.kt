package com.judopay.judokit.android.model

import cards.pay.paycardsrecognizer.sdk.Card
import com.judopay.judokit.android.ui.cardentry.model.InputModel

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

fun Card.toCardScanningResult() = CardScanningResult(
    cardNumber = cardNumber,
    cardHolder = cardHolderName,
    expirationDate = expirationDate
)

fun CardScanningResult.toInputModel() = InputModel(
    cardNumber = cardNumber,
    cardHolderName = cardHolder ?: "",
    expirationDate = expirationDate ?: ""
)
