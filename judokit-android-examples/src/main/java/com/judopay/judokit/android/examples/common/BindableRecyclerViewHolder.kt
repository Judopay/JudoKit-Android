package com.judopay.judokit.android.examples.common

interface BindableRecyclerViewHolder<V> {
    fun bind(model: V, listener: ((V) -> Unit)? = null)
}
