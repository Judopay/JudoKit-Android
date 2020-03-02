package com.judopay.ui.paymentmethods

import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.ui.paymentmethods.model.PaymentMethodSavedCardsItem

fun TokenizedCardEntity.toPaymentMethodSavedCardsItem(): PaymentMethodSavedCardsItem {
    return PaymentMethodSavedCardsItem(
            id = id,
            title = title,
            network = network,
            ending = ending,
            token = token
    )
}