package com.judokit.android.ui.paymentmethods.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.R
import com.judokit.android.ui.paymentmethods.adapter.BindableRecyclerViewHolder
import com.judokit.android.ui.paymentmethods.adapter.PaymentMethodsAdapterListener
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodGenericItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import kotlinx.android.synthetic.main.saved_card_header_item.view.editButton

class SavedCardsHeaderViewHolder(view: View) :
    RecyclerView.ViewHolder(view),
    BindableRecyclerViewHolder<PaymentMethodGenericItem, PaymentMethodItemAction> {
    override fun bind(model: PaymentMethodGenericItem, listener: PaymentMethodsAdapterListener?) =
        with(itemView) {
            if (model.isInEditMode) {
                editButton.text = resources.getString(R.string.button_done)
                editButton.setOnClickListener {
                    listener?.invoke(PaymentMethodItemAction.DONE, model)
                }
            } else {
                editButton.text = resources.getString(R.string.button_edit)
                editButton.setOnClickListener {
                    listener?.invoke(PaymentMethodItemAction.EDIT, model)
                }
            }
        }
}
