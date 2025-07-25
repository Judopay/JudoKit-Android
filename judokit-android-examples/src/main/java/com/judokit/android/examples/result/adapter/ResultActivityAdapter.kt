package com.judokit.android.examples.result.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.examples.databinding.ItemResultPropertyBinding
import com.judokit.android.examples.model.ResultItem

class ResultActivityAdapter(
    features: List<ResultItem> = emptyList(),
    private val listener: (ResultItem) -> Unit,
) : RecyclerView.Adapter<ResultActivityItemViewHolder>() {
    var items: List<ResultItem> = features
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ResultActivityItemViewHolder =
        ResultActivityItemViewHolder(ItemResultPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(
        holder: ResultActivityItemViewHolder,
        position: Int,
    ) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size
}
