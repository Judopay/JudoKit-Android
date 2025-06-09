package com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.databinding.NoSavedCardsPlaceholderItemBinding
import com.judopay.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction

class NoSavedCardsPlaceholderViewHolder(
    private val binding: NoSavedCardsPlaceholderItemBinding,
) : RecyclerView.ViewHolder(binding.root),
    BindableRecyclerViewHolder<PaymentMethodItem, PaymentMethodItemAction> {
    override fun bind(
        model: PaymentMethodItem,
        listener: PaymentMethodsAdapterListener?,
    ) = binding.addButton.setOnClickListener { listener?.invoke(PaymentMethodItemAction.ADD_CARD, model) }
}
