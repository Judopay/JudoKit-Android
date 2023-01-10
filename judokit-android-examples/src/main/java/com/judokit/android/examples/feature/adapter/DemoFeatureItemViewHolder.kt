package com.judokit.android.examples.feature.adapter

import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.examples.common.BindableRecyclerViewHolder
import com.judokit.android.examples.databinding.ItemDemoFeatureBinding
import com.judokit.android.examples.model.DemoFeature

open class DemoFeatureItemViewHolder(private val binding: ItemDemoFeatureBinding) :
    RecyclerView.ViewHolder(binding.root), BindableRecyclerViewHolder<DemoFeature> {

    override fun bind(model: DemoFeature, listener: ((DemoFeature) -> Unit)?) = with(itemView) {
        binding.titleTextView.setText(model.title)
        binding.surtitleTextView.setText(model.surTitle)
        setOnClickListener { listener?.invoke(model) }
    }
}
