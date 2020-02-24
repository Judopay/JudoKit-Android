package com.judopay.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.judopay.R

enum class PaymentMethodEnum(@DrawableRes val icon: Int, @StringRes val text:Int){
    CARD(R.drawable.ic_cards, R.string.cards),
    AMAZON_PAY(R.drawable.ic_amazonpay, R.string.amazon_pay),
    PAYPAL(R.drawable.ic_paypal, R.string.empty),
    IDEAL(R.drawable.ic_bank_ideal, R.string.ideal_payment),
    GOOGLE_PAY(R.drawable.ic_google_pay, R.string.empty)

}