package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.AppBarLayout
import com.judopay.parentOfType

class PaymentMethodsHeaderView : ConstraintLayout, AppBarLayout.OnOffsetChangedListener {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        parentOfType(AppBarLayout::class.java)?.addOnOffsetChangedListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        parentOfType(AppBarLayout::class.java)?.removeOnOffsetChangedListener(this)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

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
