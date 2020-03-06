package com.judopay.ui.paymentmethods.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.R
import com.judopay.model.displayName
import com.judopay.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodSavedCardItem
import kotlinx.android.synthetic.main.saved_card_item.view.*

class SavedCardsItemViewHolder(view: View) : RecyclerView.ViewHolder(view), BindableRecyclerViewHolder<PaymentMethodSavedCardItem, PaymentMethodItemAction> {
    override fun bind(model: PaymentMethodSavedCardItem, listener: PaymentMethodsAdapterListener?) = with(itemView) {

        title.text = model.title
        subTitle.text = context.getString(R.string.card_subtitle, model.network.displayName, model.ending)

        val checkMark = if (model.isSelected) R.drawable.ic_radio_on else R.drawable.ic_radio_off
        radioIconImageView.setImageResource(checkMark)

        setOnClickListener { listener?.invoke(PaymentMethodItemAction.PICK_CARD, model) }
    }
}
