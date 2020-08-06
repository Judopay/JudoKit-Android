package com.judokit.android.ui.paymentmethods.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.R
import com.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judokit.android.ui.paymentmethods.adapter.model.IdealBankItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judokit.android.ui.paymentmethods.adapter.model.bankResId
import com.judokit.android.ui.paymentmethods.adapter.model.drawableResId
import kotlinx.android.synthetic.main.ideal_bank_item.view.*

class IdealBankItemViewHolder(view: View) :
    RecyclerView.ViewHolder(view),
    BindableRecyclerViewHolder<IdealBankItem, PaymentMethodItemAction> {

    override fun bind(model: IdealBankItem, listener: PaymentMethodsAdapterListener?) {
        with(itemView) {
            bankImage.setImageResource(model.idealBank.drawableResId())
            bankName.text = resources.getString(model.idealBank.bankResId())
            val checkMark =
                if (model.isSelected) R.drawable.ic_radio_on else R.drawable.ic_radio_off
            radioIconImageView.visibility = View.VISIBLE
            radioIconImageView.setImageResource(checkMark)
            setOnClickListener { listener?.invoke(PaymentMethodItemAction.PICK_CARD, model) }
        }
    }
}
