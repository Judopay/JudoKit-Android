package com.judokit.android.examples.feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.examples.databinding.ItemDemoFeatureBinding
import com.judokit.android.examples.model.DemoFeature

class DemoFeaturesAdapter(features: List<DemoFeature> = emptyList(), private val listener: (DemoFeature) -> Unit) :
    RecyclerView.Adapter<DemoFeatureItemViewHolder>() {

    var features: List<DemoFeature> = features
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoFeatureItemViewHolder {
        return DemoFeatureItemViewHolder(ItemDemoFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DemoFeatureItemViewHolder, position: Int) {
        holder.bind(features[position], listener)
    }

    override fun getItemCount(): Int = features.size
}
