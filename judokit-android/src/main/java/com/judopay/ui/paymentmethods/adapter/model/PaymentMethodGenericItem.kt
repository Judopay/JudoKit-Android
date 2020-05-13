package com.judopay.ui.paymentmethods.adapter.model

data class PaymentMethodGenericItem(
    override val type: PaymentMethodItemType,
    val isInEditMode: Boolean
) : PaymentMethodItem {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentMethodGenericItem

        if (type != other.type) return false
        if (isInEditMode != other.isInEditMode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + isInEditMode.hashCode()
        return result
    }
}
