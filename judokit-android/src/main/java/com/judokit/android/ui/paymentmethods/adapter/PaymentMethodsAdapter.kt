package com.judokit.android.ui.paymentmethods.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.R
import com.judokit.android.inflate
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodDiffUtil
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemType
import com.judokit.android.ui.paymentmethods.adapter.viewholder.IdealBankItemViewHolder
import com.judokit.android.ui.paymentmethods.adapter.viewholder.MethodSelectorViewHolder
import com.judokit.android.ui.paymentmethods.adapter.viewholder.NoSavedCardsPlaceholderViewHolder
import com.judokit.android.ui.paymentmethods.adapter.viewholder.SavedCardsFooterViewHolder
import com.judokit.android.ui.paymentmethods.adapter.viewholder.SavedCardsHeaderViewHolder
import com.judokit.android.ui.paymentmethods.adapter.viewholder.SavedCardsItemViewHolder

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
            val diffCallback = PaymentMethodDiffUtil(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val type = PaymentMethodItemType.values().firstOrNull { it.ordinal == viewType }

        return when (type) {
            PaymentMethodItemType.SELECTOR -> MethodSelectorViewHolder(parent.inflate(R.layout.payment_methods_selector_item))
            PaymentMethodItemType.SAVED_CARDS_HEADER -> SavedCardsHeaderViewHolder(parent.inflate(R.layout.saved_card_header_item))
            PaymentMethodItemType.SAVED_CARDS_ITEM -> SavedCardsItemViewHolder(parent.inflate(R.layout.saved_card_item))
            PaymentMethodItemType.SAVED_CARDS_FOOTER -> SavedCardsFooterViewHolder(parent.inflate(R.layout.saved_card_footer_item))
            PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER -> NoSavedCardsPlaceholderViewHolder(parent.inflate(R.layout.no_saved_cards_placeholder_item))
            PaymentMethodItemType.IDEAL_BANK_ITEM -> IdealBankItemViewHolder(parent.inflate(R.layout.ideal_bank_item))
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
