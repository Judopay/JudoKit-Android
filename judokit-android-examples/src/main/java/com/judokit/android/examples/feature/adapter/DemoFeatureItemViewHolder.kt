package com.judokit.android.examples.feature.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.examples.common.BindableRecyclerViewHolder
import com.judokit.android.examples.model.DemoFeature
import kotlinx.android.synthetic.main.item_demo_feature.view.*

open class DemoFeatureItemViewHolder(view: View) :
    RecyclerView.ViewHolder(view),
    BindableRecyclerViewHolder<DemoFeature> {
    override fun bind(model: DemoFeature, listener: ((DemoFeature) -> Unit)?) = with(itemView) {
        titleTextView.setText(model.title)
        surtitleTextView.setText(model.surTitle)
        setOnClickListener { listener?.invoke(model) }
    }
}
