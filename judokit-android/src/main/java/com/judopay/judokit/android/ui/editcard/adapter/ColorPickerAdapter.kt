package com.judopay.judokit.android.ui.editcard.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.R
import com.judopay.judokit.android.inflate
import com.judopay.judokit.android.ui.editcard.CardPattern

data class ColorPickerItem(
    val pattern: CardPattern,
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
