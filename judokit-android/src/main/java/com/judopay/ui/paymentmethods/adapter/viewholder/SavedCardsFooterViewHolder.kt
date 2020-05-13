package com.judopay.ui.paymentmethods.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judopay.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import kotlinx.android.synthetic.main.saved_card_footer_item.view.*

class SavedCardsFooterViewHolder(view: View) : RecyclerView.ViewHolder(view), BindableRecyclerViewHolder<PaymentMethodItem, PaymentMethodItemAction> {
    override fun bind(model: PaymentMethodItem, listener: PaymentMethodsAdapterListener?) = with(itemView) {
        addCardButton.setOnClickListener { listener?.invoke(PaymentMethodItemAction.ADD_CARD, model) }
    }
}
