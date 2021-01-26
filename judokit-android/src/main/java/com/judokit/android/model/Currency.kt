package com.judokit.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents all the currencies that can be used when performing transactions with the judo API.
 */
@Parcelize
enum class Currency : Parcelable {
    AED,
    AUD,
    BRL,
    CAD,
    CHF,
    CZK,
    DKK,
    EUR,
    GBP,
    HKD,
    HUF,
    JPY,
    NOK,
    NZD,
    PKR,
    PLN,
    SEK,
    SGD,
    QAR,
    SAR,
    USD,
    ZAR
}
