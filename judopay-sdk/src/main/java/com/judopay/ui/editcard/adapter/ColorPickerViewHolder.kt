package com.judopay.ui.editcard.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.judopay.R
import com.judopay.ui.editcard.colorRes
import kotlinx.android.synthetic.main.color_picker_item.view.*

private const val SCALE_SELECTED = 1.4f
private const val SCALE_UNSELECTED = 1f

class ColorPickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(model: ColorPickerItem, listener: ColorPickerAdapterListener?) = with(itemView) {
        val cornerRadius = context.resources.getDimension(R.dimen.corner_radius_2_8dp)
        val scale = if (model.isSelected) SCALE_SELECTED else SCALE_UNSELECTED
        val padding = if (model.isSelected) resources.getDimension(R.dimen.space_12)
            .toInt() else resources.getDimension(R.dimen.space_4).toInt()

        ValueAnimator.ofInt(colorContainer.paddingStart, padding).apply {
            addUpdateListener {
                colorContainer.setPadding(Integer.parseInt(it.animatedValue.toString()))
            }
            start()
        }
        ObjectAnimator.ofFloat(colorContainer, "scaleX", scale).start()
        ObjectAnimator.ofFloat(colorContainer, "scaleY", scale).start()

        colorPreview.apply {
            background = drawableWith(model.pattern.colorRes(this.context), cornerRadius)
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
