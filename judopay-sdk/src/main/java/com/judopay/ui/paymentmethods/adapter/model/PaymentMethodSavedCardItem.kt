package com.judopay.ui.paymentmethods.adapter.model

import com.judopay.model.CardNetwork

data class PaymentMethodSavedCardItem(
    override val type: PaymentMethodItemType = PaymentMethodItemType.SAVED_CARDS_ITEM,
    val id: Int,
    val title: String,
    val network: CardNetwork,
    val ending: String,
    val token: String,
    val expireDate: String,
    var isSelected: Boolean = false
) : PaymentMethodItem {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as PaymentMethodSavedCardItem

                if (type != other.type) return false
                if (id != other.id) return false
                if (title != other.title) return false
                if (network != other.network) return false
                if (ending != other.ending) return false
                if (token != other.token) return false
                if (expireDate != other.expireDate) return false
                if (isSelected != other.isSelected) return false

                return true
        }

        override fun hashCode(): Int {
                var result = type.hashCode()
                result = 31 * result + id
                result = 31 * result + title.hashCode()
                result = 31 * result + network.hashCode()
                result = 31 * result + ending.hashCode()
                result = 31 * result + token.hashCode()
                result = 31 * result + expireDate.hashCode()
                result = 31 * result + isSelected.hashCode()
                return result
        }
}
