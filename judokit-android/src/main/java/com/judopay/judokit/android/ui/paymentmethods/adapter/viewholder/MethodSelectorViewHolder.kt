package com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem
import kotlinx.android.synthetic.main.payment_methods_selector_item.view.*

class MethodSelectorViewHolder(view: View) :
    RecyclerView.ViewHolder(view),
    BindableRecyclerViewHolder<PaymentMethodItem, PaymentMethodItemAction> {

    override fun bind(
        model: PaymentMethodItem,
        listener: PaymentMethodsAdapterListener?
    ) {

        (model as? PaymentMethodSelectorItem)?.let { item ->
            itemView.slider.setPaymentMethods(item.paymentMethods, item.currentSelected) { method ->
                item.currentSelected = method
                listener?.invoke(PaymentMethodItemAction.SELECT_PAYMENT_METHOD, item)
            }
        }
    }
}
