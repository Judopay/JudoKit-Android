package com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.databinding.PaymentMethodsSelectorItemBinding
import com.judopay.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodSelectorItem

class MethodSelectorViewHolder(private val binding: PaymentMethodsSelectorItemBinding) :
    RecyclerView.ViewHolder(binding.root), BindableRecyclerViewHolder<PaymentMethodItem, PaymentMethodItemAction> {
    override fun bind(
        model: PaymentMethodItem,
        listener: PaymentMethodsAdapterListener?,
    ) {
        (model as? PaymentMethodSelectorItem)?.let { item ->
            binding.slider.setPaymentMethods(item.paymentMethods, item.currentSelected) { method ->
                item.currentSelected = method
                listener?.invoke(PaymentMethodItemAction.SELECT_PAYMENT_METHOD, item)
            }
        }
    }
}
