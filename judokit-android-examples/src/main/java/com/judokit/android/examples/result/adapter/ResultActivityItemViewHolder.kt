package com.judokit.android.examples.result.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.examples.common.BindableRecyclerViewHolder
import com.judokit.android.examples.databinding.ItemResultPropertyBinding
import com.judokit.android.examples.model.ResultItem

class ResultActivityItemViewHolder(
    private val binding: ItemResultPropertyBinding,
) : RecyclerView.ViewHolder(binding.root),
    BindableRecyclerViewHolder<ResultItem> {
    override fun bind(
        model: ResultItem,
        listener: ((ResultItem) -> Unit)?,
    ) = with(itemView) {
        binding.nameTextView.text = model.title
        binding.valueTextView.tag = model.title
        binding.valueTextView.text = model.value

        if (model.subResult != null) {
            binding.arrowIcon.visibility = View.VISIBLE
            binding.valueTextView.visibility = View.GONE

            setOnLongClickListener(null)
            setOnClickListener { listener?.invoke(model) }
        } else {
            binding.arrowIcon.visibility = View.GONE
            binding.valueTextView.visibility = View.VISIBLE

            setOnClickListener(null)
            setOnLongClickListener {
                listener?.invoke(model)
                return@setOnLongClickListener true
            }
        }
    }
}
