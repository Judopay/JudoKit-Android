package com.judopay.ui.paymentmethods.model

import com.judopay.model.PaymentMethod

data class PaymentMethodSelectorItem(
        override val type: PaymentMethodItemType = PaymentMethodItemType.SELECTOR,
        val paymentMethods: List<PaymentMethod>,
        var currentSelected: PaymentMethod) : PaymentMethodItem {

    override fun toString(): String {
        return "PaymentMethodSelectorItem(type=$type, paymentMethods=$paymentMethods, currentSelected=$currentSelected)"
    }
}
