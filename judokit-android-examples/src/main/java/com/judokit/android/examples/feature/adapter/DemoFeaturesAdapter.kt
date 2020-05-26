package com.judokit.android.examples.feature.adapter

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.judokit.android.examples.R
import com.judokit.android.examples.common.inflate
import com.judokit.android.examples.feature.adapter.DemoFeatureItemViewHolder.DemoFeatureItemCustomViewHolder
import com.judokit.android.examples.model.DemoFeature
import com.judokit.android.examples.model.isCustomButton

@LayoutRes
private const val RES_LAYOUT = R.layout.item_demo_feature

@LayoutRes
private const val RES_LAYOUT_CUSTOM_BUTTON = R.layout.item_demo_feature_custom_button

class DemoFeaturesAdapter(
    features: List<DemoFeature> = emptyList(),
    private val listener: (DemoFeature) -> Unit
) : RecyclerView.Adapter<DemoFeatureItemViewHolder>() {

    var features: List<DemoFeature> = features
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return if (features[position].isCustomButton) {
            RES_LAYOUT_CUSTOM_BUTTON
        } else {
            RES_LAYOUT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoFeatureItemViewHolder {
        return when (viewType) {
            RES_LAYOUT -> DemoFeatureItemViewHolder(parent.inflate(R.layout.item_demo_feature))
            else -> DemoFeatureItemCustomViewHolder(parent.inflate(R.layout.item_demo_feature_custom_button))
        }
    }

    override fun onBindViewHolder(holder: DemoFeatureItemViewHolder, position: Int) {
        holder.bind(features[position], listener)
    }

    override fun getItemCount(): Int = features.size
}
