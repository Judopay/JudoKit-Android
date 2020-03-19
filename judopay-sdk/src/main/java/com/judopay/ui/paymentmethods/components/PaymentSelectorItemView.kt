package com.judopay.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import com.judopay.R
import com.judopay.inflate
import com.judopay.model.PaymentMethod
import kotlinx.android.synthetic.main.payment_selector_item.view.*

class PaymentSelectorItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0

) : LinearLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.payment_selector_item, true)
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
