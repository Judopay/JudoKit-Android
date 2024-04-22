package com.judopay.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.judopay.judokit.android.R
import com.judopay.judokit.android.inflate
import com.judopay.judokit.android.ui.paymentmethods.model.CardViewModel
import com.judopay.judokit.android.ui.paymentmethods.model.CardViewType

open class GooglePayCardViewModel(
    override val type: CardViewType = CardViewType.GOOGLE_PAY,
    override var layoutId: Int = R.id.googlePayCardView,
) : CardViewModel

class GooglePayCardView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : CardView(context, attrs, defStyle) {
        init {
            inflate(R.layout.google_pay_card_view, true)
        }
    }
