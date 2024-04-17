package com.judopay.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import com.judopay.judokit.android.databinding.PaymentSelectorItemBinding
import com.judopay.judokit.android.model.PaymentMethod

class PaymentSelectorItemView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : LinearLayout(context, attrs, defStyle) {
        private val binding = PaymentSelectorItemBinding.inflate(LayoutInflater.from(context), this, true)

        private lateinit var paymentMethod: PaymentMethod

        fun setPaymentMethod(paymentMethod: PaymentMethod) {
            this.paymentMethod = paymentMethod
        }

        fun getPaymentMethod() = paymentMethod

        fun setImage(
            @DrawableRes resource: Int,
        ) {
            binding.paymentImageView.setImageResource(resource)
        }

        fun setText(
            @StringRes text: Int,
        ) {
            binding.paymentTextView.text = context.getString(text)
        }

        fun setTextVisibility(visibility: Int) {
            binding.paymentTextView.visibility = visibility
        }

        fun isTextEmpty(): Boolean = binding.paymentTextView.text.isEmpty()

        fun getTextView(): TextView = binding.paymentTextView

        fun getImageView(): AppCompatImageView = binding.paymentImageView
    }
