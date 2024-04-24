package com.judopay.judokit.android.ui.paymentmethods.adapter.model

import androidx.recyclerview.widget.DiffUtil

class PaymentMethodDiffUtil(
    private val oldList: List<PaymentMethodItem>,
    private val newList: List<PaymentMethodItem>,
) : DiffUtil.Callback() {
    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean = oldList[oldItemPosition].type == newList[newItemPosition].type

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean = oldList[oldItemPosition] == newList[newItemPosition]
}
