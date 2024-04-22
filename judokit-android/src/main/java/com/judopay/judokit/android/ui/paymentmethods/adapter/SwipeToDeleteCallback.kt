package com.judopay.judokit.android.ui.paymentmethods.adapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.paymentmethods.adapter.viewholder.SavedCardsItemViewHolder
import java.util.Locale

abstract class SwipeToDeleteCallback(
    @ColorRes val backgroundColor: Int = R.color.tomato_red,
    @StringRes val text: Int = R.string.delete,
    @ColorRes val textColor: Int = R.color.white,
    @FontRes val textFont: Int = R.font.sf_pro_display_regular,
    @DimenRes val textSize: Int = R.dimen.body,
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private val background = ColorDrawable()
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        return makeMovementFlags(0, if (viewHolder is SavedCardsItemViewHolder) ItemTouchHelper.LEFT else 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val itemWidth = itemView.right - itemView.left
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            canvas.drawRect(
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat(),
                clearPaint,
            )
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the red delete background
        background.color = ContextCompat.getColor(itemView.context, backgroundColor)
        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(canvas)

        // Draw the delete icon
        val paint = Paint()
        paint.color = ContextCompat.getColor(itemView.context, textColor)
        paint.style = Paint.Style.FILL

        val resources = itemView.context.resources
        val marginEnd = resources.getDimension(R.dimen.space_24)

        paint.textSize = resources.getDimension(textSize)
        paint.typeface = ResourcesCompat.getFont(itemView.context, textFont)

        val textToDraw = itemView.context.getString(text).uppercase(Locale.getDefault())

        val bounds = Rect()
        paint.getTextBounds(textToDraw, 0, textToDraw.length, bounds)
        val height = bounds.height()
        val with = bounds.width()

        val y = itemView.top + itemHeight / 2 + height / 2
        val x = itemWidth - with - marginEnd

        canvas.drawText(textToDraw, x, y.toFloat(), paint)

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
