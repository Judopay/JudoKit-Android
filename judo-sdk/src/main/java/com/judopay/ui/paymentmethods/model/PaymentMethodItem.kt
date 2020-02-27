package com.judopay.ui.paymentmethods.model

import com.judopay.model.CardNetwork
import com.judopay.model.PaymentMethod

data class PaymentMethodSelectorItem(
        override val type: PaymentMethodItemType = PaymentMethodItemType.SELECTOR,
        val paymentMethods: List<PaymentMethod>,
        var currentSelected: PaymentMethod) : PaymentMethodItem {

    override fun toString(): String {
        return "PaymentMethodSelectorItem(type=$type, paymentMethods=$paymentMethods, currentSelected=$currentSelected)"
    }
}

data class PaymentMethodSavedCardsItem(
        override val type: PaymentMethodItemType = PaymentMethodItemType.SAVED_CARDS_ITEM,
        val id: Int,
        val title: String,
        val network: CardNetwork,
        val ending: String
) : PaymentMethodItem

data class PaymentMethodGenericItem(override val type: PaymentMethodItemType) : PaymentMethodItem

interface PaymentMethodItem {
    val type: PaymentMethodItemType
}