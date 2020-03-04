package com.judopay.ui.paymentmethods.model

import com.judopay.model.CardNetwork

data class PaymentMethodSavedCardsItem(
        override val type: PaymentMethodItemType = PaymentMethodItemType.SAVED_CARDS_ITEM,
        val id: Int,
        val title: String,
        val network: CardNetwork,
        val ending: String,
        val token: String,
        val expireDate: String
) : PaymentMethodItem
