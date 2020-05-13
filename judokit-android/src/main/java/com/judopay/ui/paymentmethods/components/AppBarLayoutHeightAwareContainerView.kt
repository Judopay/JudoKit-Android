package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.AppBarLayout
import com.judopay.parentOfType

class AppBarLayoutHeightAwareContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle), AppBarLayout.OnOffsetChangedListener {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        parentOfType(AppBarLayout::class.java)?.addOnOffsetChangedListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        parentOfType(AppBarLayout::class.java)?.removeOnOffsetChangedListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val newHeight = appBarLayout.layoutParams.height + verticalOffset

        var margins = 0

        (layoutParams as? MarginLayoutParams)?.let {
            margins = it.topMargin + it.bottomMargin
        }

        layoutParams.height = newHeight - margins

        if (!isInLayout) {
            requestLayout()
        }
    }
}
