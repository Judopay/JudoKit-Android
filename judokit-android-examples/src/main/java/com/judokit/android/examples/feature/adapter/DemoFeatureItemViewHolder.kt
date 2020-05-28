package com.judokit.android.examples.feature.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.examples.common.BindableRecyclerViewHolder
import com.judokit.android.examples.model.DemoFeature
import com.judokit.android.ui.common.PayByBankButton
import kotlinx.android.synthetic.main.item_demo_feature.view.*
import kotlinx.android.synthetic.main.item_demo_feature_custom_button.view.*

open class DemoFeatureItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
    BindableRecyclerViewHolder<DemoFeature> {
    override fun bind(model: DemoFeature, listener: ((DemoFeature) -> Unit)?) = with(itemView) {
        titleTextView.setText(model.title)
        surtitleTextView.setText(model.surTitle)
        setOnClickListener { listener?.invoke(model) }
    }
}

class DemoFeatureItemCustomViewHolder(view: View) : DemoFeatureItemViewHolder(view) {
    override fun bind(model: DemoFeature, listener: ((DemoFeature) -> Unit)?) = with(itemView) {
        val button = when (model) {
            DemoFeature.PAY_BY_BANK_APP ->
                PayByBankButton(context).apply {
                    setOnClickListener { listener?.invoke(model) }
                }
            else -> null
        }
        customButtonContainer.addView(button)
        payByBankButton = button
    }

    companion object {
        var payByBankButton: PayByBankButton? = null
    }
}
