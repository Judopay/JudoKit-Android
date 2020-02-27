package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import com.judopay.R
import com.judopay.model.PaymentMethod
import kotlinx.android.synthetic.main.item_payment_selector.view.paymentImageView
import kotlinx.android.synthetic.main.item_payment_selector.view.paymentTextView

class PaymentSelectorItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0

) : LinearLayout(context, attrs, defStyle) {
    init {
        LayoutInflater.from(context).inflate(R.layout.item_payment_selector, this)
    }

    private lateinit var paymentMethod: PaymentMethod

    fun setPaymentMethod(paymentMethod: PaymentMethod) {
        this.paymentMethod = paymentMethod
    }

    fun getPaymentMethod() = paymentMethod

    fun setImage(@DrawableRes resource: Int) {
        paymentImageView.setImageResource(resource)
    }

    fun setText(@StringRes text: Int) {
        paymentTextView.text = context.getString(text)
    }

    fun setTextVisibility(visibility: Int) {
        paymentTextView.visibility = visibility
    }

    fun isTextEmpty(): Boolean = paymentTextView.text.isEmpty()

    fun getTextView(): TextView = paymentTextView

    fun getImageView(): AppCompatImageView = paymentImageView
}