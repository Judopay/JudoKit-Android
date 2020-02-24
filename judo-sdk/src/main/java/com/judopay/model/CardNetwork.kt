package com.judopay.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class CardNetwork : Parcelable {
    VISA,
    MASTER_CARD,
    MAESTRO,
    AMEX,
    CHINA_UNION_PAY,
    JCB,
    DISCOVER,
    DINERS_CLUB
}
