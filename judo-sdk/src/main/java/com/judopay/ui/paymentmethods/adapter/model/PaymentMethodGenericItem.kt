package com.judopay.ui.paymentmethods.adapter.model

data class PaymentMethodGenericItem(override val type: PaymentMethodItemType) : PaymentMethodItem {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentMethodGenericItem

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}
