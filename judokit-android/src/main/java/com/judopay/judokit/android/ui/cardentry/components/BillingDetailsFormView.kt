package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.judopay.judokit.android.R
import com.judopay.judokit.android.inflate
import com.judopay.judokit.android.ui.cardentry.model.FormModel
import com.judopay.judokit.android.ui.cardentry.model.InputModel
import kotlinx.android.synthetic.main.billing_details_form_view.view.*
import kotlinx.android.synthetic.main.card_entry_form_view.view.*
import kotlinx.android.synthetic.main.card_entry_fragment.view.*

class BillingDetailsFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    internal lateinit var onBillingDetailsSubmitButtonClickListener: () -> Unit
    internal lateinit var onBillingDetailsBackButtonClickListener: () -> Unit

    internal var model = FormModel(
        InputModel(),
        emptyList(),
        emptyList()
    )
        set(value) {
            field = value
            billingDetailsSubmitButton.state = model.buttonState
        }

    init {
        inflate(R.layout.billing_details_form_view, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        billingDetailsBackButton.setOnClickListener { onBillingDetailsBackButtonClickListener.invoke() }
        billingDetailsSubmitButton.setOnClickListener { onBillingDetailsSubmitButtonClickListener.invoke() }
    }
}
