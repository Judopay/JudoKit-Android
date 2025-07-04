package com.judopay.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.judopay.judokit.android.databinding.ViewPaymentSelectorBinding
import com.judopay.judokit.android.model.PaymentMethod
import com.judopay.judokit.android.model.icon
import com.judopay.judokit.android.model.text
import com.judopay.judokit.android.subViewsWithType
import kotlin.collections.forEach as kForEach

private const val MARGIN_12 = 12
private const val MARGIN_54 = 54

typealias PaymentSelectorViewSelectionListener = (selected: PaymentMethod) -> Unit

class PaymentSelectorView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : HorizontalScrollView(context, attrs, defStyle) {
        private val binding = ViewPaymentSelectorBinding.inflate(LayoutInflater.from(context), this, true)

        private var currentSelected: PaymentMethod? = null
        private var previousSelected: PaymentSelectorItemView? = null
        private var lastUsedSelected = false

        @Suppress("MagicNumber", "LongMethod")
        fun setPaymentMethods(
            paymentMethods: List<PaymentMethod>,
            currentSelected: PaymentMethod?,
            onClick: PaymentSelectorViewSelectionListener,
        ) {
            binding.paymentSelectorContainer.subViewsWithType(PaymentSelectorItemView::class.java).kForEach {
                binding.paymentSelectorContainer.removeView(it)
            }

            this.currentSelected = currentSelected
            val itemViews: MutableList<PaymentSelectorItemView> = mutableListOf()
            val ids: MutableList<Int> = mutableListOf()
            overScrollMode = View.OVER_SCROLL_NEVER

            paymentMethods.kForEach { paymentMethod ->
                val itemView =
                    PaymentSelectorItemView(context).apply {
                        if (paymentMethods.size < 3) {
                            layoutParams =
                                ViewGroup.LayoutParams(
                                    0,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                )
                            gravity = Gravity.CENTER
                        }
                        id = View.generateViewId()
                        setImage(paymentMethod.icon)
                        setText(paymentMethod.text)
                        setPaymentMethod(paymentMethod)
                        elevation = 10f
                        translationZ = 10f
                        tag = paymentMethod.name
                    }
                binding.paymentSelectorContainer.addView(itemView)
                itemViews.add(itemView)
                ids.add(itemView.id)
            }

            val set = ConstraintSet()
            set.clone(binding.paymentSelectorContainer)

            itemViews.forEachIndexed { index, itemView ->
                if (currentSelected != null && paymentMethods.contains(currentSelected)) {
                    if (index == 0) {
                        set.setMargin(itemView.id, ConstraintSet.START, MARGIN_12)
                    }
                    if (currentSelected == itemView.getPaymentMethod()) {
                        previousSelected = itemView
                        selectItem(set, itemView)
                    }
                } else if (index == 0) {
                    previousSelected = itemView
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
                    if (previousSelected != itemView || !lastUsedSelected) {
                        previousSelected?.setTextVisibility(View.GONE)
                        selectItem(set, itemView)
                        set.applyTo(binding.paymentSelectorContainer)
                        TransitionManager.beginDelayedTransition(binding.paymentSelectorContainer)
                        scrollToView(itemView)
                        onClick.invoke(itemView.getPaymentMethod())
                        previousSelected = itemView
                    }
                }
            }
            chainViews(ids, set)
            set.applyTo(binding.paymentSelectorContainer)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            while (width != 0 && !lastUsedSelected) {
                previousSelected?.callOnClick()
                lastUsedSelected = true
            }
        }

        private fun selectItem(
            set: ConstraintSet,
            itemView: PaymentSelectorItemView,
        ) {
            if (!itemView.isTextEmpty()) {
                itemView.setTextVisibility(View.VISIBLE)
            }
            set.apply {
                connect(binding.selector.id, ConstraintSet.TOP, itemView.id, ConstraintSet.TOP)
                connect(binding.selector.id, ConstraintSet.BOTTOM, itemView.id, ConstraintSet.BOTTOM)
                connect(binding.selector.id, ConstraintSet.START, itemView.id, ConstraintSet.START)
                connect(binding.selector.id, ConstraintSet.END, itemView.id, ConstraintSet.END)
            }
        }

        private fun chainViews(
            itemViews: MutableList<Int>,
            set: ConstraintSet,
        ) {
            if (itemViews.size > 2) {
                set.createHorizontalChain(
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.RIGHT,
                    itemViews.toIntArray(),
                    null,
                    ConstraintSet.CHAIN_SPREAD_INSIDE,
                )
            } else {
                set.createHorizontalChain(
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.RIGHT,
                    itemViews.toIntArray(),
                    null,
                    ConstraintSet.CHAIN_SPREAD,
                )
            }
        }

        private fun scrollToView(itemView: PaymentSelectorItemView) {
            val rect = Rect()
            itemView
                .getTextView()
                .measure(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
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
