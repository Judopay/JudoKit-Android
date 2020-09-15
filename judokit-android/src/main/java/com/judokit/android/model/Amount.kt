package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.requireNotNull
import com.judokit.android.requireNotNullOrEmpty
import kotlinx.android.parcel.Parcelize
import java.text.NumberFormat
import java.util.Locale

/**
 * Amount objects store information about an amount and the corresponding currency for a transaction.
 * Use [Builder] to create an instance.
 */
@Parcelize
class Amount internal constructor(val amount: String, val currency: Currency) : Parcelable {

    /**
     * Builder class for creating an instance of Amount.
     */
    class Builder {
        private var amount: String? = null
        private var currency: Currency? = null

        /**
         * Sets the amount. Can be an empty string if necessary
         */
        fun setAmount(amount: String?) = apply { this.amount = amount }

        /**
         * Sets the currency.
         * @see Currency for all supported currencies
         */
        fun setCurrency(currency: Currency?) = apply { this.currency = currency }

        /**
         * Creates an instance of Amount based on provided data in setters.
         * @throws IllegalStateException If:
         * - the provided amount is not a number;
         * - the provided currency is null.
         */
        fun build(): Amount {
            val myAmount: String?
            if (amount.isNullOrEmpty())
                myAmount = ""
            else {
                myAmount = requireNotNullOrEmpty(amount, "amount")
                check(myAmount.matches("^[0-9]+(\\.[0-9][0-9])?\$".toRegex())) { "amount should be a number" }
            }

            val myCurrency = requireNotNull(currency, "currency")

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
