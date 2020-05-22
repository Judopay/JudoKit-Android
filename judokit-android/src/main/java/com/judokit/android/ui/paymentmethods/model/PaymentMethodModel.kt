package com.judokit.android.ui.paymentmethods.model

import com.judokit.android.model.PaymentMethod
import com.judokit.android.ui.paymentmethods.adapter.model.IdealBank
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem

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

data class PayByBankPaymentMethodModel(
    override val type: PaymentMethod = PaymentMethod.PAY_BY_BANK,
    override val items: List<PaymentMethodItem>
) : PaymentMethodModel
