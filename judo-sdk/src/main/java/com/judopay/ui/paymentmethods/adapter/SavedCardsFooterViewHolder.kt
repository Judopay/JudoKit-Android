package com.judopay.ui.paymentmethods.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.ui.paymentmethods.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.model.PaymentMethodItemAction
import kotlinx.android.synthetic.main.saved_card_footer_item.view.*

class SavedCardsFooterViewHolder(view: View) : RecyclerView.ViewHolder(view), BindableRecyclerViewHolder<PaymentMethodItem, PaymentMethodItemAction> {
    override fun bind(model: PaymentMethodItem, listener: PaymentMethodsAdapterListener?) = with(itemView) {
        addCardButton.setOnClickListener { listener?.invoke(model, PaymentMethodItemAction.ADD_CARD) }
    }
}
