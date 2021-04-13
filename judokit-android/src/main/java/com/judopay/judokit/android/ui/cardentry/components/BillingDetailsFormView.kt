package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.judopay.judokit.android.R
import com.judopay.judokit.android.inflate

class BillingDetailsFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.billing_details_form_view, true)
    }
}
