package com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.IdealBankItemBinding
import com.judopay.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.IdealBankItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction

class IdealBankItemViewHolder(private val binding: IdealBankItemBinding) :
    RecyclerView.ViewHolder(binding.root), BindableRecyclerViewHolder<IdealBankItem, PaymentMethodItemAction> {

    override fun bind(model: IdealBankItem, listener: PaymentMethodsAdapterListener?) {
        with(itemView) {
            binding.bankImage.setImageResource(model.idealBank.drawableResId)
            binding.bankName.text = model.idealBank.title
            val checkMark = if (model.isSelected) R.drawable.ic_radio_on else R.drawable.ic_radio_off
            binding.radioIconImageView.visibility = View.VISIBLE
            binding.radioIconImageView.setImageResource(checkMark)
            setOnClickListener { listener?.invoke(PaymentMethodItemAction.PICK_CARD, model) }
        }
    }
}
