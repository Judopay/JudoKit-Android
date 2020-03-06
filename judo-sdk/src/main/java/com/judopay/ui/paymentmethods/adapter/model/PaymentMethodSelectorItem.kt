package com.judopay.ui.paymentmethods.adapter.model

import com.judopay.model.PaymentMethod

data class PaymentMethodSelectorItem(
        override val type: PaymentMethodItemType = PaymentMethodItemType.SELECTOR,
        val paymentMethods: List<PaymentMethod>,
        var currentSelected: PaymentMethod) : PaymentMethodItem
