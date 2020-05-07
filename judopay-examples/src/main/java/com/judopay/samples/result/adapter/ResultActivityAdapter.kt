package com.judopay.samples.result.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judopay.samples.R
import com.judopay.samples.common.inflate
import com.judopay.samples.model.ResultItem

class ResultActivityAdapter(
    features: List<ResultItem> = emptyList(),
    private val listener: (ResultItem) -> Unit
) : RecyclerView.Adapter<ResultActivityItemViewHolder>() {

    var items: List<ResultItem> = features
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResultActivityItemViewHolder {
        return ResultActivityItemViewHolder(parent.inflate(R.layout.item_result_property))
    }

    override fun onBindViewHolder(holder: ResultActivityItemViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int = items.size
}