package com.judopay.ui.paymentmethods.model

import com.judopay.model.PaymentMethod
import com.judopay.ui.paymentmethods.adapter.model.IdealBank
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem

interface PaymentMethodModel {
    val type: PaymentMethod
    val items: List<PaymentMethodItem>
}

data class CardPaymentMethodModel(
    override val type: PaymentMethod = PaymentMethod.CARD,
    override val items: List<PaymentMethodItem>,
    val selectedCard: PaymentMethodSavedCardItem?
) : PaymentMethodModel

data class GooglePayPaymentMethodModel(
    override val type: PaymentMethod = PaymentMethod.GOOGLE_PAY,
    override val items: List<PaymentMethodItem>
) : PaymentMethodModel

data class IdealPaymentMethodModel(
    override val type: PaymentMethod = PaymentMethod.IDEAL,
    override val items: List<PaymentMethodItem>,
    val selectedBank: IdealBank
) : PaymentMethodModel
