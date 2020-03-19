package com.judopay.ui.editcard.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.judopay.R
import kotlinx.android.synthetic.main.color_picker_item.view.*

class ColorPickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(model: ColorPickerItem, listener: ColorPickerAdapterListener?) = with(itemView) {
        val cornerRadius = context.resources.getDimension(R.dimen.corner_radius_2_8dp)
        val size = if (model.isSelected) context.resources.getDimension(R.dimen.size_50dp)
            .toInt() else context.resources.getDimension(R.dimen.size_36dp).toInt()
        val params = colorPreview.layoutParams

        colorPreview.apply {
            layoutParams = params.apply {
                height = size
                width = size
            }
            background = drawableWith(model.color, cornerRadius)
        }

        setOnClickListener { listener?.invoke(model) }
    }
}

private fun drawableWith(fillColor: Int, cornerRadius: Float): Drawable {
    val shapeModel = ShapeAppearanceModel.Builder()
        .setAllCorners(CornerFamily.ROUNDED, cornerRadius)
        .build()

    val fillColorStateList = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_enabled)),
        intArrayOf(fillColor)
    )
    val shape = MaterialShapeDrawable(shapeModel)
    shape.fillColor = fillColorStateList
    return shape
}