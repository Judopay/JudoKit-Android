package com.judopay.ui.paymentmethods.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.ui.paymentmethods.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.model.PaymentMethodItemAction
import com.judopay.ui.paymentmethods.model.PaymentMethodSavedCardsItem
import kotlinx.android.synthetic.main.saved_card_item.view.*

class SavedCardsItemViewHolder(view: View) : RecyclerView.ViewHolder(view), BindableRecyclerViewHolder<PaymentMethodSavedCardsItem, PaymentMethodItemAction> {
    override fun bind(model: PaymentMethodSavedCardsItem, listener: PaymentMethodsAdapterListener?) = with(itemView) {

        title.text = model.title
        subTitle.text = "Visa ending ${model.ending}" // TODO: load the template from resources
        setOnClickListener { listener?.invoke(PaymentMethodItemAction.PICK_CARD, model) }
    }
}
