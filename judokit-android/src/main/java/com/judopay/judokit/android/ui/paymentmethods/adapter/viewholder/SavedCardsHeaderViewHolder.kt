package com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.SavedCardHeaderItemBinding
import com.judopay.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction

class SavedCardsHeaderViewHolder(private val binding: SavedCardHeaderItemBinding) :
    RecyclerView.ViewHolder(binding.root), BindableRecyclerViewHolder<PaymentMethodGenericItem, PaymentMethodItemAction> {
    override fun bind(
        model: PaymentMethodGenericItem,
        listener: PaymentMethodsAdapterListener?,
    ) = with(binding.root) {
        if (model.isInEditMode) {
            binding.editButton.text = resources.getString(R.string.jp_button_done)
            binding.editButton.setOnClickListener {
                listener?.invoke(PaymentMethodItemAction.DONE, model)
            }
        } else {
            binding.editButton.text = resources.getString(R.string.jp_button_edit)
            binding.editButton.setOnClickListener {
                listener?.invoke(PaymentMethodItemAction.EDIT, model)
            }
        }
    }
}
