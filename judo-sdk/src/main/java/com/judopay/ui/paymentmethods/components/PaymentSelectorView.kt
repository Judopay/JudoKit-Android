package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.children
import androidx.transition.TransitionManager
import com.judopay.R
import com.judopay.inflate
import com.judopay.model.PaymentMethod
import com.judopay.model.icon
import com.judopay.model.text
import com.judopay.subViewsWithType
import kotlinx.android.synthetic.main.payment_methods_selector_item.view.*
import kotlinx.android.synthetic.main.view_payment_selector.view.*

private const val MARGIN_12 = 12
private const val MARGIN_54 = 54

typealias PaymentSelectorViewSelectionListener = (selected: PaymentMethod) -> Unit

class PaymentSelectorView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : HorizontalScrollView(context, attrs, defStyle) {

    init {
        inflate(R.layout.view_payment_selector, true)
    }

    private var currentSelected: PaymentMethod? = null
    private var prevClicked: PaymentSelectorItemView? = null
    private var lastUsedSelected = false

    fun setPaymentMethods(
            paymentMethods: List<PaymentMethod>,
            currentSelected: PaymentMethod?,
            onClick: PaymentSelectorViewSelectionListener
    ) {
        container.subViewsWithType(PaymentSelectorItemView::class.java).forEach {
            container.removeView(it)
        }

        this.currentSelected = currentSelected
        val itemViews: MutableList<PaymentSelectorItemView> = mutableListOf()
        val ids: MutableList<Int> = mutableListOf()
        overScrollMode = View.OVER_SCROLL_NEVER

        paymentMethods.forEach { paymentMethod ->
            val itemView = PaymentSelectorItemView(context).apply {
                if (paymentMethods.size < 3) {
                    layoutParams = ViewGroup.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    gravity = Gravity.CENTER
                }
                id = View.generateViewId()
                setImage(paymentMethod.icon)
                setText(paymentMethod.text)
                setPaymentMethod(paymentMethod)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    elevation = 10f
                    translationZ = 10f
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
                    set.setMargin(itemView.id, ConstraintSet.START, MARGIN_12)
                }
                if (currentSelected == itemView.getPaymentMethod()) {
                    prevClicked = itemView
                    selectItem(set, itemView)
                }
            } else if (index == 0) {
                prevClicked = itemView
                selectItem(set, itemView)
                set.setMargin(itemView.id, ConstraintSet.START, MARGIN_12)
            }
            if (index == itemViews.size - 1) {
                set.setMargin(itemView.id, ConstraintSet.END, MARGIN_12)
            } else {
                set.setMargin(itemView.id, ConstraintSet.END, MARGIN_54)
            }
            set.centerVertically(itemView.id, ConstraintSet.PARENT_ID)
            itemView.setOnClickListener {
                if (prevClicked != itemView || !lastUsedSelected) {
                    prevClicked?.setTextVisibility(View.GONE)
                    selectItem(set, itemView)
                    set.applyTo(container)
                    prevClicked = itemView
                    TransitionManager.beginDelayedTransition(container)
                    scrollToView(itemView)
                    onClick.invoke(itemView.getPaymentMethod())
                }
            }
        }
        chainViews(ids, set)
        set.applyTo(container)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        while (width != 0 && !lastUsedSelected) {
            prevClicked?.callOnClick()
            lastUsedSelected = true
        }
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
                    ConstraintSet.CHAIN_SPREAD
            )
        }
    }

    private fun scrollToView(itemView: PaymentSelectorItemView) {
        val rect = Rect()
        itemView.getTextView()
                .measure(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        if (!(itemView.getGlobalVisibleRect(rect) && itemView.height == rect.height() && itemView.width == rect.width())) {
            val itemViewWidth = itemView.getImageView().width + itemView.getTextView().measuredWidth
            if (currentSelected != null && !lastUsedSelected) {
                smoothScrollTo(itemView.left, 0)
                lastUsedSelected = true
            } else if (scrollX < itemView.x.toInt()) {
                smoothScrollTo(scrollX + itemViewWidth, 0)
            } else {
                smoothScrollTo(scrollX - itemViewWidth, 0)
            }
        }
    }
}
