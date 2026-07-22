package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GooglePayRecurrencePeriod : Parcelable {
    YEAR,
    MONTH,
    WEEK,
    DAY,
}

@Parcelize
data class GooglePayIntroductoryPeriodInfo(
    val introductoryPeriodStartDateTime: String? = null,
    val introductoryPeriodEndDateTime: String,
    val label: String,
    val totalPrice: String,
) : Parcelable

@Parcelize
data class GooglePayRecurrencePeriodItem(
    val billingInitialDateTime: String? = null,
    val billingFinalDateTime: String? = null,
    val label: String,
    val price: String? = null,
    val priceStatus: GooglePayPriceStatus,
    val recurrencePeriod: GooglePayRecurrencePeriod,
    val recurrencePeriodCount: Int,
) : Parcelable

@Parcelize
data class GooglePayRecurringParameters(
    val immediateTotalPrice: String,
    val recurrenceItems: List<GooglePayRecurrencePeriodItem>,
    val introductoryPeriodInfo: GooglePayIntroductoryPeriodInfo? = null,
    val tokenUpdateUrl: String? = null,
    val managementUrl: String? = null,
    val billingAgreement: String? = null,
) : Parcelable

@Parcelize
data class GooglePayRecurringTransactionInfo(
    val currencyCode: String,
    val countryCode: String,
    val transactionId: String?,
    val tokenUpdateUrl: String?,
    val managementUrl: String?,
    val billingAgreement: String?,
    val immediateTotalPrice: String,
    val introductoryPeriodInfo: GooglePayIntroductoryPeriodInfo?,
    val recurrenceItems: List<GooglePayRecurrencePeriodItem>,
) : Parcelable
