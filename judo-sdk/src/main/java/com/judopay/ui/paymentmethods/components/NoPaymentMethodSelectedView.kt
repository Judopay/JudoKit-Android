package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.judopay.R
import com.judopay.inflate
import com.judopay.ui.paymentmethods.model.CardViewModel
import com.judopay.ui.paymentmethods.model.CardViewType

data class NoPaymentMethodSelectedViewModel(
        override val type: CardViewType = CardViewType.PLACEHOLDER
) : CardViewModel

class NoPaymentMethodSelectedView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.no_payment_method_selected_view, true)
    }

}