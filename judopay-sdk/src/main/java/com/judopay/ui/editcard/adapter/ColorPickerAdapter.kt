package com.judopay.ui.editcard.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judopay.R
import com.judopay.inflate

data class ColorPickerItem(
    val color: Int,
    var isSelected: Boolean = false
)

typealias ColorPickerAdapterListener = (item: ColorPickerItem) -> Unit

class ColorPickerAdapter(
    items: List<ColorPickerItem> = emptyList(),
    private val listener: ColorPickerAdapterListener
) : RecyclerView.Adapter<ColorPickerViewHolder>() {

    var items: List<ColorPickerItem> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ColorPickerViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPickerViewHolder {
        return ColorPickerViewHolder(parent.inflate(R.layout.color_picker_item))
    }
}
