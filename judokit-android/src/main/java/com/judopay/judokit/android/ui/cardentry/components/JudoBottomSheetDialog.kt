package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.judopay.judokit.android.R
import com.judopay.judokit.android.dismissKeyboard

class JudoBottomSheetDialog(
    context: Context,
    @StyleRes theme: Int,
) : BottomSheetDialog(context, theme) {
    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? =
        object : BottomSheetBehavior.BottomSheetCallback() {
            var keyboardDismissed = false

            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float,
            ) {
                if (slideOffset < 0 && !keyboardDismissed) {
                    bottomSheet.dismissKeyboard()
                    keyboardDismissed = true
                }
            }

            override fun onStateChanged(
                bottomSheet: View,
                newState: Int,
            ) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    keyboardDismissed = false
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED ||
                    newState == BottomSheetBehavior.STATE_HIDDEN
                ) {
                    bottomSheet.dismissKeyboard()
                }
            }
        }

    private val bottomSheet: FrameLayout?
        get() = findViewById(com.google.android.material.R.id.design_bottom_sheet)

    private val bottomSheetBehavior: BottomSheetBehavior<FrameLayout>?
        get() = bottomSheet?.let { BottomSheetBehavior.from(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Light status bar toggle, because it's computed like so:
        // `boolean light = MaterialColors.isLightBackground(view.getBackground());`
        bottomSheet?.setBackgroundColor(Color.WHITE)
        setOnShowListener {
            subscribeToBottomSheetEvents()
            bottomSheet?.let { setBackgroundAppearance(it) }
        }
    }

    override fun dismiss() {
        unsubscribeFromBottomSheetEvents()
        super.dismiss()
    }

    private fun subscribeToBottomSheetEvents() = bottomSheetCallback?.let { behavior.addBottomSheetCallback(it) }

    private fun unsubscribeFromBottomSheetEvents() =
        bottomSheetCallback?.let {
            bottomSheetBehavior?.removeBottomSheetCallback(it)
            bottomSheetCallback = null
        }

    private fun setBackgroundAppearance(bottomSheet: FrameLayout) =
        with(bottomSheet) {
            val cornerSize = context.resources.getDimension(R.dimen.size_16dp)
            val shapeAppearanceModel =
                ShapeAppearanceModel
                    .builder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                    .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
                    .build()

            clipChildren = true
            clipToPadding = true
            clipToOutline = true
            background =
                MaterialShapeDrawable(shapeAppearanceModel).apply {
                    fillColor = ColorStateList.valueOf(Color.WHITE)
                }
        }
}
