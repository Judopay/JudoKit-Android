package com.judopay.samples.result.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judopay.samples.common.BindableRecyclerViewHolder
import com.judopay.samples.model.ResultItem
import kotlinx.android.synthetic.main.item_result_property.view.*

class ResultActivityItemViewHolder(view: View) : RecyclerView.ViewHolder(view),
    BindableRecyclerViewHolder<ResultItem> {

    override fun bind(model: ResultItem, listener: ((ResultItem) -> Unit)?) = with(itemView) {
        nameTextView.text = model.title
        valueTextView.text = model.value

        if (model.subResult != null) {
            arrowIcon.visibility = View.VISIBLE
            valueTextView.visibility = View.GONE

            setOnLongClickListener(null)
            setOnClickListener { listener?.invoke(model) }
        } else {
            arrowIcon.visibility = View.GONE
            valueTextView.visibility = View.VISIBLE

            setOnClickListener(null)
            setOnLongClickListener {
                listener?.invoke(model)
                return@setOnLongClickListener true
            }
        }
    }
}
