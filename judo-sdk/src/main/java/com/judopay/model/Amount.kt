package com.judopay.model

import android.os.Parcelable
import com.judopay.requireNotNull
import com.judopay.requireNotNullOrEmpty
import kotlinx.android.parcel.Parcelize

@Parcelize
class Amount internal constructor(val amount: String, val currency: Currency) : Parcelable {

    class Builder {
        private var amount: String? = null
        private var currency: Currency? = null

        fun setAmount(amount: String?) = apply { this.amount = amount }
        fun setCurrency(currency: Currency?) = apply { this.currency = currency }

        fun build(): Amount {
            val myAmount = requireNotNullOrEmpty(amount, "amount")
            check(myAmount.matches("-?\\d+(\\.\\d+)?".toRegex())) { "amount should be a number" }

            val myCurrency = requireNotNull(currency, "currency")

            return Amount(myAmount, myCurrency)
        }
    }
}
