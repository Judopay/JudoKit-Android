package com.judopay.ui.editcard

import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.ui.paymentmethods.model.PaymentCardViewModel

fun TokenizedCardEntity.toPaymentCardViewModel(newTitle: String, selectedPattern: CardPattern) =
    PaymentCardViewModel(
        id = id,
        cardNetwork = network,
        name = newTitle,
        maskedNumber = ending,
        expireDate = expireDate,
        pattern = selectedPattern
    )