package com.judopay.ui.paymentmethods.adapter.model

import com.judopay.model.CardNetwork
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemType

data class PaymentMethodSavedCardItem(
        override val type: PaymentMethodItemType = PaymentMethodItemType.SAVED_CARDS_ITEM,
        val id: Int,
        val title: String,
        val network: CardNetwork,
        val ending: String,
        val token: String,
        val expireDate: String,
        var isSelected: Boolean = false
) : PaymentMethodItem
