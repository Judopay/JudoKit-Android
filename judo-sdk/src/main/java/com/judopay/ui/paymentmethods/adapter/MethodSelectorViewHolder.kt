package com.judopay.ui.paymentmethods.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.model.PaymentMethodEnum
import kotlinx.android.synthetic.main.payment_methods_selector_item.view.slider

class MethodSelectorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(
        list: List<PaymentMethodEnum>,
        lastUsed: PaymentMethodEnum,
        listener: PaymentMethodSelectedListener?
    ) = with(itemView) {
        slider.setPaymentTypes(list, lastUsed) { listener?.invoke(it) }
    }
}
