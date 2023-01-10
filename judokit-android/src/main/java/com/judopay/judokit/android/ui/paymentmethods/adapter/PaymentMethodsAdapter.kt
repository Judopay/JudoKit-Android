package com.judopay.judokit.android.ui.paymentmethods.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.databinding.IdealBankItemBinding
import com.judopay.judokit.android.databinding.NoSavedCardsPlaceholderItemBinding
import com.judopay.judokit.android.databinding.PaymentMethodsSelectorItemBinding
import com.judopay.judokit.android.databinding.SavedCardFooterItemBinding
import com.judopay.judokit.android.databinding.SavedCardHeaderItemBinding
import com.judopay.judokit.android.databinding.SavedCardItemBinding
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodDiffUtil
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItem
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemAction
import com.judopay.judokit.android.ui.paymentmethods.adapter.model.PaymentMethodItemType
import com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder.IdealBankItemViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder.MethodSelectorViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder.NoSavedCardsPlaceholderViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder.SavedCardsFooterViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder.SavedCardsHeaderViewHolder
import com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder.SavedCardsItemViewHolder

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
        return when (PaymentMethodItemType.values().firstOrNull { it.ordinal == viewType }) {
            PaymentMethodItemType.SELECTOR -> MethodSelectorViewHolder(
                PaymentMethodsSelectorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            PaymentMethodItemType.SAVED_CARDS_HEADER -> SavedCardsHeaderViewHolder(
                SavedCardHeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            PaymentMethodItemType.SAVED_CARDS_ITEM -> SavedCardsItemViewHolder(
                SavedCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            PaymentMethodItemType.SAVED_CARDS_FOOTER -> SavedCardsFooterViewHolder(
                SavedCardFooterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            PaymentMethodItemType.NO_SAVED_CARDS_PLACEHOLDER -> NoSavedCardsPlaceholderViewHolder(
                NoSavedCardsPlaceholderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            PaymentMethodItemType.IDEAL_BANK_ITEM -> IdealBankItemViewHolder(
                IdealBankItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> {
                throw NotImplementedError("Unsupported or null type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        val viewHolder = holder as? BindableRecyclerViewHolder<PaymentMethodItem, PaymentMethodItemAction>
        viewHolder?.bind(items[position], listener)
    }

    override fun getItemViewType(position: Int): Int = items[position].type.ordinal

    override fun getItemCount(): Int = items.size
}
