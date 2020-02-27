package com.judopay.ui.paymentmethods.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.ui.paymentmethods.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.model.PaymentMethodItemAction
import com.judopay.ui.paymentmethods.model.PaymentMethodSelectorItem
import kotlinx.android.synthetic.main.payment_methods_selector_item.view.*

class MethodSelectorViewHolder(view: View) : RecyclerView.ViewHolder(view),
        BindableRecyclerViewHolder<PaymentMethodItem, PaymentMethodItemAction> {

    override fun bind(model: PaymentMethodItem,
                      listener: PaymentMethodsAdapterListener?) {

        (model as? PaymentMethodSelectorItem)?.let { item ->
            itemView.slider.setPaymentMethods(item.paymentMethods, item.currentSelected) { method ->
                item.currentSelected = method
                listener?.invoke(PaymentMethodItemAction.SELECT_PAYMENT_METHOD, item)
            }
        }
    }

}
