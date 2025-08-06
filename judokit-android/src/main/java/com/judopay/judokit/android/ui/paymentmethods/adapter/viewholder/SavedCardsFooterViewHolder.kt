package com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.databinding.SavedCardFooterItemBinding
import com.judopay.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction

class SavedCardsFooterViewHolder(
    private val binding: SavedCardFooterItemBinding,
) : RecyclerView.ViewHolder(binding.root),
    BindableRecyclerViewHolder<PaymentMethodItem, PaymentMethodItemAction> {
    override fun bind(
        model: PaymentMethodItem,
        listener: PaymentMethodsAdapterListener?,
    ) = binding.addCardButton.setOnClickListener { listener?.invoke(PaymentMethodItemAction.ADD_CARD, model) }
}
