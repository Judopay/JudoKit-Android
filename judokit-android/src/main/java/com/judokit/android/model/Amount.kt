package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.requireNotNull
import com.judokit.android.requireNotNullOrEmpty
import kotlinx.android.parcel.Parcelize
import java.text.NumberFormat
import java.util.Locale

@Parcelize
class Amount internal constructor(val amount: String, val currency: Currency) : Parcelable {

    class Builder {
        private var amount: String? = null
        private var currency: Currency? = null

        fun setAmount(amount: String?) = apply { this.amount = amount }
        fun setCurrency(currency: Currency?) = apply { this.currency = currency }

        fun build(): Amount {
            val myAmount: String?
            if (amount.isNullOrEmpty())
                myAmount = ""
            else {
                myAmount = requireNotNullOrEmpty(amount, "amount")
                check(myAmount.matches("^[0-9]+(\\.[0-9][0-9])?\$".toRegex())) { "The amount specified should be a positive number. The amount parameter has either not been set or has an incorrect format." }
            }

            val myCurrency = requireNotNull(
                currency,
                "currency",
                "Currency cannot be null or empty. The required Currency parameter has not been set in the Judo configuration."
            )

            return Amount(myAmount, myCurrency)
        }
    }

    override fun toString(): String {
        return "Amount(amount='$amount', currency=$currency)"
    }
}

val Amount.formatted: String
    get() {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            format.maximumFractionDigits = 2
            format.currency = java.util.Currency.getInstance(currency.name)
            format.format(amount.toBigDecimal())
        } catch (exception: IllegalArgumentException) {
            exception.printStackTrace()
            "${currency.name} $amount"
        }
    }
