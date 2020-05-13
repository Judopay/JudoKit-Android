package com.judopay.ui.paymentmethods.model

import com.judopay.R
import com.judopay.model.CardNetwork
import com.judopay.ui.editcard.CardPattern

data class PaymentCardViewModel(
    override val type: CardViewType = CardViewType.CARD,
    override var layoutId: Int = R.id.cardView,
    val id: Int = 0,
    val cardNetwork: CardNetwork = CardNetwork.VISA,
    val name: String = "",
    val maskedNumber: String = "",
    val expireDate: String = "",
    val pattern: CardPattern = CardPattern.BLACK
) : CardViewModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaymentCardViewModel

        if (type != other.type) return false
        if (id != other.id) return false
        if (cardNetwork != other.cardNetwork) return false
        if (name != other.name) return false
        if (maskedNumber != other.maskedNumber) return false
        if (expireDate != other.expireDate) return false
        if (pattern != other.pattern) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + id
        result = 31 * result + cardNetwork.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + maskedNumber.hashCode()
        result = 31 * result + expireDate.hashCode()
        result = 31 * result + pattern.hashCode()
        return result
    }
}
