package com.judopay.judokit.android.ui.cardentry.components

import android.content.Context
import android.util.AttributeSet
import android.widget.ViewAnimator

class CardEntryViewAnimator
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : ViewAnimator(context, attrs) {
        interface OnViewWillAppearListener {
            fun onViewWillAppear()
        }

        override fun setDisplayedChild(whichChild: Int) {
            super.setDisplayedChild(whichChild)

            val child = getChildAt(whichChild)

            if (child is OnViewWillAppearListener) {
                child.onViewWillAppear()
            }
        }
    }
