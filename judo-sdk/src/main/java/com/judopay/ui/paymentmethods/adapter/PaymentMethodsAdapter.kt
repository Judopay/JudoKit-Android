package com.judopay.ui.paymentmethods.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judopay.R
import com.judopay.inflate
import com.judopay.ui.paymentmethods.adapter.viewholder.*
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.ui.paymentmethods.adapter.model.PaymentMethodItemType

internal interface BindableRecyclerViewHolder<V, A> {
    fun bind(model: V, listener: PaymentMethodsAdapterListener? = null)
}

typealias PaymentMethodsAdapterListener = (action: PaymentMethodItemAction, item: PaymentMethodItem) -> Unit

class PaymentMethodsAdapter(
        items: List<PaymentMethodItem> = emptyList(),
        private val listener: PaymentMethodsAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<PaymentMethodItem> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val type = PaymentMethodItemType.values().firstOrNull { it.ordinal == viewType }

        return when (type) {
            PaymentMethodItemType.SELECTOR -> MethodSelectorViewHolder(parent.inflate(R.layout.payment_methods_selector_item))
            PaymentMethodItemType.SAVED_CARDS_HEADER -> SavedCardsHeaderViewHolder(parent.inflate(R.layout.saved_card_header_item))
            PaymentMethodItemType.SAVED_CARDS_ITEM -> SavedCardsItemViewHolder(parent.inflate(R.layout.saved_card_item))
            PaymentMethodItemType.SAVED_CARDS_FOOTER -> SavedCardsFooterViewHolder(parent.inflate(R.layout.saved_card_footer_item))
            PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER -> NoSavedCardsPlaceholderViewHolder(parent.inflate(R.layout.no_saved_cards_placeholder_item))
            else -> {
                throw NotImplementedError("Unsupported or null type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as? BindableRecyclerViewHolder<PaymentMethodItem, PaymentMethodItemAction>
        viewHolder?.bind(items[position], listener)
    }

    override fun getItemViewType(position: Int): Int = items[position].type.ordinal

    override fun getItemCount(): Int = items.size

}
