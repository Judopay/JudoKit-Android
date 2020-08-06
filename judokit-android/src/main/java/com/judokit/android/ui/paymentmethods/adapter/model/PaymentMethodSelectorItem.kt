package com.judokit.android.ui.paymentmethods.adapter.model

import com.judokit.android.model.PaymentMethod

data class PaymentMethodSelectorItem(
    override val type: PaymentMethodItemType = PaymentMethodItemType.SELECTOR,
    val paymentMethods: List<PaymentMethod>,
    var currentSelected: PaymentMethod
) : PaymentMethodItem {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentMethodSelectorItem

        if (type != other.type) return false
        if (paymentMethods != other.paymentMethods) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + paymentMethods.hashCode()
        result = 31 * result + currentSelected.hashCode()
        return result
    }
}
