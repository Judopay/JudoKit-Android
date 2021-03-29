package com.judopay.judokit.android.ui.paymentmethods.model

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.IdealBank

open class IdealPaymentCardViewModel(
    override val type: CardViewType = CardViewType.IDEAL,
    override var layoutId: Int = R.id.idealPaymentCardView,
    val idealBank: IdealBank = IdealBank.ING_BANK
) : CardViewModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdealPaymentCardViewModel

        if (type != other.type) return false
        if (idealBank != other.idealBank) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + idealBank.hashCode()
        return result
    }
}
