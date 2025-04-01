package com.judopay.judokit.android.ui.paymentmethods.model

import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem

interface PaymentMethodModel {
    val type: PaymentMethod
    val items: List<PaymentMethodItem>
}

data class CardPaymentMethodModel(
    override val type: PaymentMethod = PaymentMethod.CARD,
    override val items: List<PaymentMethodItem>,
    val selectedCard: PaymentMethodSavedCardItem?,
) : PaymentMethodModel

data class GooglePayPaymentMethodModel(
    override val type: PaymentMethod = PaymentMethod.GOOGLE_PAY,
    override val items: List<PaymentMethodItem>,
) : PaymentMethodModel
