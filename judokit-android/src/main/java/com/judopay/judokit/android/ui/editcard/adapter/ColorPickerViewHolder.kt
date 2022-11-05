package com.judopay.judokit.android.ui.editcard.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.ColorPickerItemBinding
import com.judopay.judokit.android.ui.editcard.colorRes

private const val SCALE_SELECTED = 1.4f
private const val SCALE_UNSELECTED = 1f

class ColorPickerViewHolder(private val binding: ColorPickerItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: ColorPickerItem, listener: ColorPickerAdapterListener?) = with(itemView) {
        val cornerRadius = context.resources.getDimension(R.dimen.corner_radius_2_8dp)
        val scale = if (model.isSelected) SCALE_SELECTED else SCALE_UNSELECTED
        val padding = if (model.isSelected) resources.getDimension(R.dimen.space_12)
            .toInt() else resources.getDimension(R.dimen.space_4).toInt()

        ValueAnimator.ofInt(binding.colorContainer.paddingStart, padding).apply {
            addUpdateListener {
                binding.colorContainer.setPadding(Integer.parseInt(it.animatedValue.toString()))
            }
            start()
        }
        ObjectAnimator.ofFloat(binding.colorContainer, "scaleX", scale).start()
        ObjectAnimator.ofFloat(binding.colorContainer, "scaleY", scale).start()

        binding.colorPreview.apply {
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
        arrayOf(intArrayOf(-android.R.attr.state_enabled)),
        intArrayOf(fillColor)
    )

    val shape = MaterialShapeDrawable(shapeModel)
    shape.fillColor = fillColorStateList
    return shape
}
