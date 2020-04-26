package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.judopay.R
import com.judopay.inflate
import com.judopay.ui.paymentmethods.model.CardViewModel
import com.judopay.ui.paymentmethods.model.CardViewType

open class PayByBankCardViewModel(
    override val type: CardViewType = CardViewType.PAY_BY_BANK,
    override var layoutId: Int = R.id.payByBankCardView
) : CardViewModel

class PayByBankCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CardView(context, attrs, defStyle) {

    init {
        inflate(R.layout.pay_by_bank_card_view, true)
    }
}
