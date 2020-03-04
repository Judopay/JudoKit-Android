package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.judopay.R
import com.judopay.inflate

class NoPaymentMethodSelectedView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.no_payment_method_selected_view, true)
    }

}