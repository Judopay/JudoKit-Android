package com.judopay.ui.paymentmethods

import com.judopay.persistence.entity.TokenizedCardEntity
import com.judopay.ui.paymentmethods.model.PaymentMethodSavedCardsItem

fun TokenizedCardEntity.toPaymentMethodSavedCardsItem(): PaymentMethodSavedCardsItem {
    return PaymentMethodSavedCardsItem(
            id = id,
            title = title,
            network = network,
            ending = maskedNumber
    )
}