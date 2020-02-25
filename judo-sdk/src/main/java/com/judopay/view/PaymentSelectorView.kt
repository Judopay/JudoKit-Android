package com.judopay.view

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.HorizontalScrollView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.judopay.R
import com.judopay.model.PaymentMethod
import com.judopay.model.icon
import com.judopay.model.text
import kotlinx.android.synthetic.main.view_payment_selector.view.container
import kotlinx.android.synthetic.main.view_payment_selector.view.selector

private const val MARGIN_10 = 10
private const val MARGIN_54 = 54

typealias PaymentSelectorViewSelectionListener = (selected: PaymentMethod) -> Unit

class PaymentSelectorView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : HorizontalScrollView(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_payment_selector, this)
    }

    private var currentSelected: PaymentMethod? = null

    fun setPaymentMethods(
            paymentMethods: List<PaymentMethod>,
            currentSelected: PaymentMethod?,
            onClick: PaymentSelectorViewSelectionListener
    ) {
        this.currentSelected = currentSelected
        val itemViews: MutableList<PaymentSelectorItemView> = mutableListOf()
        val ids: MutableList<Int> = mutableListOf()
        var prevClicked: PaymentSelectorItemView? = null

        paymentMethods.forEach { paymentMethod ->
            val itemView = PaymentSelectorItemView(context).apply {
                id = View.generateViewId()
                setImage(paymentMethod.icon)
                setText(paymentMethod.text)
                setPaymentMethod(paymentMethod)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    elevation = 16f
                    translationZ = 16f
                }
            }
            container.addView(itemView)
            itemViews.add(itemView)
            ids.add(itemView.id)
        }

        val set = ConstraintSet()
        set.clone(container)

        itemViews.forEachIndexed { index, itemView ->
            if (currentSelected != null && paymentMethods.contains(currentSelected)) {
                if (index == 0) {
                    set.setMargin(itemView.id, ConstraintSet.START, MARGIN_10)
                }
                if (currentSelected == itemView.getPaymentMethod()) {
                    prevClicked = itemView
                }
            } else if (index == 0) {
                prevClicked = itemView
                selectItem(set, itemView)
                set.setMargin(itemView.id, ConstraintSet.START, MARGIN_10)
            }
            if (index == itemViews.size - 1) {
                set.setMargin(itemView.id, ConstraintSet.END, MARGIN_10)
            } else {
                set.setMargin(itemView.id, ConstraintSet.END, MARGIN_54)
            }
            set.centerVertically(itemView.id, ConstraintSet.PARENT_ID)
            itemView.setOnClickListener {
                prevClicked?.setTextVisibility(View.GONE)
                selectItem(set, itemView)
                set.applyTo(container)
                prevClicked = itemView
                TransitionManager.beginDelayedTransition(container)
                scrollToView(itemView)
                onClick.invoke(itemView.getPaymentMethod())
            }
        }
        chainViews(ids, set)
        set.applyTo(container)
    }

    private fun selectItem(set: ConstraintSet, itemView: PaymentSelectorItemView) {
        if (!itemView.isTextEmpty()) {
            itemView.setTextVisibility(View.VISIBLE)
        }
        set.apply {
            connect(selector.id, ConstraintSet.TOP, itemView.id, ConstraintSet.TOP)
            connect(selector.id, ConstraintSet.BOTTOM, itemView.id, ConstraintSet.BOTTOM)
            connect(selector.id, ConstraintSet.START, itemView.id, ConstraintSet.START)
            connect(selector.id, ConstraintSet.END, itemView.id, ConstraintSet.END)
        }
    }

    private fun chainViews(itemViews: MutableList<Int>, set: ConstraintSet) {
        if (itemViews.size > 2) {
            set.createHorizontalChain(
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.RIGHT,
                    itemViews.toIntArray(),
                    null,
                    ConstraintSet.CHAIN_SPREAD_INSIDE
            )
        } else {
            set.createHorizontalChain(
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.RIGHT,
                    itemViews.toIntArray(),
                    null,
                    ConstraintSet.CHAIN_PACKED
            )
        }
    }

    private var lastUsedSelected = false
    private fun scrollToView(itemView: PaymentSelectorItemView) {
        val rect = Rect()
        if (!(itemView.getGlobalVisibleRect(rect) && itemView.height == rect.height() && itemView.width == rect.width())) {
            if (currentSelected != null && !lastUsedSelected) {
                smoothScrollTo(itemView.right, 0)
                lastUsedSelected = true
            } else if (scrollX < itemView.x.toInt()) {
                smoothScrollTo(
                        scrollX + (itemView.getImageView().width + itemView.getTextView().width),
                        0
                )
            } else {
                smoothScrollTo(
                        scrollX - (itemView.getImageView().width + itemView.getTextView().width),
                        0
                )
            }
        }

    }
}

