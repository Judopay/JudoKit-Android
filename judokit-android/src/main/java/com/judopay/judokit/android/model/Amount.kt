package com.judopay.judokit.android.model

import android.os.Parcelable
import com.judopay.judokit.android.model.Amount.Builder
import com.judopay.judokit.android.requireNotNull
import com.judopay.judokit.android.requireNotNullOrEmpty
import com.judopay.judokit.android.trimIndent
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat
import java.util.Locale

/**
 * Amount objects store information about an amount and the corresponding currency for a transaction.
 * Use [Builder] to create an instance.
 */
@Parcelize
class Amount internal constructor(
    val amount: String,
    val currency: Currency,
) : Parcelable {
    /**
     * Builder class for creating an instance of [Amount].
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
         * @param currency Accepts one of the values from [Currency] enum.
         */
        fun setCurrency(currency: Currency?) = apply { this.currency = currency }

        /**
         * Creates an instance of [Amount] based on provided data in setters.
         * @throws IllegalStateException If the provided amount is not a number
         * @throws IllegalArgumentException If the provided currency is null.
         * @return an [Amount] object.
         */
        fun build(): Amount {
            val myAmount: String?
            if (amount.isNullOrEmpty()) {
                myAmount = ""
            } else {
                myAmount = requireNotNullOrEmpty(amount, "amount")
                check(myAmount.matches("^[0-9]+(\\.[0-9][0-9])?\$".toRegex())) {
                    """
                    The amount specified should be a positive number.
                    The amount parameter has either not been set or has an incorrect format.
                    """.trimIndent(true)
                }
            }

            val myCurrency =
                requireNotNull(
                    currency,
                    "currency",
                    "Currency cannot be null or empty. The required Currency parameter has not been set in the Judo configuration.",
                )

            return Amount(myAmount, myCurrency)
        }
    }

    override fun toString(): String = "Amount(amount='$amount', currency=$currency)"
}

@Suppress("SwallowedException")
val Amount.formatted: String
    get() {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            format.maximumFractionDigits = 2
            format.currency = java.util.Currency.getInstance(currency.name)
            format.format(amount.toBigDecimal())
        } catch (exception: IllegalArgumentException) {
            "${currency.name} $amount"
        }
    }
