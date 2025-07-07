package com.judopay.judokit.android.ui.editcard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.databinding.ColorPickerItemBinding
import com.judopay.judokit.android.ui.editcard.CardPattern

data class ColorPickerItem(
    val pattern: CardPattern,
    var isSelected: Boolean = false,
)

typealias ColorPickerAdapterListener = (item: ColorPickerItem) -> Unit

class ColorPickerAdapter(
    items: List<ColorPickerItem> = emptyList(),
    private val listener: ColorPickerAdapterListener,
) : RecyclerView.Adapter<ColorPickerViewHolder>() {
    var items: List<ColorPickerItem> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: ColorPickerViewHolder,
        position: Int,
    ) {
        holder.bind(items[position], listener)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ColorPickerViewHolder = ColorPickerViewHolder(ColorPickerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}
