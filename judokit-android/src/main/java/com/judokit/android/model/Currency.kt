package com.judokit.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents all the currencies that can be used when performing transactions with the judo API.
 */
@Parcelize
enum class Currency : Parcelable {
    GBP,
    USD,
    EUR,
    AUD,
    SEK,
    CAD,
    NOK,
    BRL,
    CHF,
    CZK,
    DKK,
    HKD,
    HUF,
    JPY,
    NZD,
    PLN,
    ZAR,
    ARS,
    BHD,
    MMK,
    KYD,
    CLP,
    CNY,
    COP,
    ISK,
    INR,
    IDR,
    JOD,
    KWD,
    OMR,
    NGN,
    PKR,
    NIO,
    PAB,
    PHP,
    QAR,
    RUB,
    SAR,
    SGD,
    VND,
    AED,
    RSD,
    RON,
    MXN,
    UAH
}
