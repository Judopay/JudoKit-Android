package com.judokit.android.examples.feature.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.examples.common.BindableRecyclerViewHolder
import com.judokit.android.examples.model.DemoFeature
import com.judokit.android.examples.model.displayButton
import kotlinx.android.synthetic.main.item_demo_feature.view.*

open class DemoFeatureItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
    BindableRecyclerViewHolder<DemoFeature> {
    override fun bind(model: DemoFeature, listener: ((DemoFeature) -> Unit)?) = with(itemView) {
        titleTextView.setText(model.title)
        surtitleTextView.setText(model.surTitle)
        val button = model.displayButton(context)
        if (button != null) {
            button.setOnClickListener { listener?.invoke(model) }
            displayButton.addView(button)
        }
        setOnClickListener { listener?.invoke(model) }
    }
}
