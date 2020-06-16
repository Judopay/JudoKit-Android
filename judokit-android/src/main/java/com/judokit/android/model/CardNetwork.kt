package com.judokit.android.model

import android.os.Parcelable
import com.judokit.android.R
import com.judokit.android.model.CardNetwork.Companion.DEFAULT_CARD_NUMBER_MASK
import com.judokit.android.withWhitespacesRemoved
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class CardNetwork : Parcelable {
    VISA,
    MASTERCARD,
    MAESTRO,
    AMEX,
    CHINA_UNION_PAY,
    JCB,
    DISCOVER,
    DINERS_CLUB,
    OTHER;

    companion object {
        const val DEFAULT_CARD_NUMBER_MASK = "#### #### #### ####"

        private val REGEX_VISA = "^4\\d{0,15}".toRegex()
        private val REGEX_MASTERCARD = "^(5[1-5]\\d{0,2}|22[2-9]\\d{0,1}|2[3-7]\\d{0,2})\\d{0,12}".toRegex()
        private val REGEX_MAESTRO = "^(?:5[0678]\\d{0,2}|6304|67\\d{0,2})\\d{0,12}".toRegex()
        private val REGEX_AMEX = "^3[47]\\d{0,13}".toRegex()
        private val REGEX_DISCOVER = "^(?:6011|65\\d{0,2}|64[4-9]\\d?)\\d{0,12}".toRegex()
        private val REGEX_DINERS_CLUB = "^3(?:0([0-5]|9)|[689]\\d?)\\d{0,11}".toRegex()
        private val REGEX_JCB = "^(?:35\\d{0,2})\\d{0,12}".toRegex()
        private val REGEX_CHINA_UNION_PAY = "^(62|81)\\d{0,14}".toRegex()

        fun ofNumber(number: String): CardNetwork {
            val sanitizedNumber = number.withWhitespacesRemoved
            return when {
                sanitizedNumber.matches(REGEX_VISA) -> VISA
                sanitizedNumber.matches(REGEX_MASTERCARD) -> MASTERCARD
                sanitizedNumber.matches(REGEX_MAESTRO) -> MAESTRO
                sanitizedNumber.matches(REGEX_AMEX) -> AMEX
                sanitizedNumber.matches(REGEX_DISCOVER) -> DISCOVER
                sanitizedNumber.matches(REGEX_DINERS_CLUB) -> DINERS_CLUB
                sanitizedNumber.matches(REGEX_JCB) -> JCB
                sanitizedNumber.matches(REGEX_CHINA_UNION_PAY) -> CHINA_UNION_PAY
                else -> OTHER
            }
        }

        fun withIdentifier(id: Int): CardNetwork = when (id) {
            1 /*VISA*/,
            3 /*VISA_ELECTRON*/,
            11 /*VISA_DEBIT*/ -> VISA
            2 -> MASTERCARD
            10 -> MAESTRO
            8 -> AMEX
            7 -> CHINA_UNION_PAY
            9 -> JCB
            12,
            14 -> DISCOVER
            13 -> DINERS_CLUB
            else -> OTHER
        }
    }
}

val CardNetwork.cardNumberMask: String
    get() = when (this) {
        CardNetwork.AMEX -> "#### ###### #####"
        CardNetwork.DINERS_CLUB -> "#### ###### ####"
        else -> DEFAULT_CARD_NUMBER_MASK
    }

val CardNetwork.securityCodeNumberMask: String
    get() = if (this == CardNetwork.AMEX) "####" else "###"

val CardNetwork.securityCodeLength: Int
    get() = if (this == CardNetwork.AMEX) 4 else 3

val CardNetwork.securityCodeName: String
    get() = when (this) {
        CardNetwork.AMEX -> "CID"
        CardNetwork.VISA -> "CVV2"
        CardNetwork.MASTERCARD -> "CVC2"
        CardNetwork.CHINA_UNION_PAY -> "CVN2"
        CardNetwork.JCB -> "CAV2"
        else -> "CVV"
    }

val CardNetwork.displayName: String
    get() = when (this) {
        CardNetwork.VISA -> "Visa"
        CardNetwork.MASTERCARD -> "Master Card"
        CardNetwork.MAESTRO -> "Maestro"
        CardNetwork.AMEX -> "AmEx"
        CardNetwork.CHINA_UNION_PAY -> "China UnionPay"
        CardNetwork.JCB -> "JCB"
        CardNetwork.DISCOVER -> "Discover"
        CardNetwork.DINERS_CLUB -> "Diner's Club"
        CardNetwork.OTHER -> "Unknown Card Network"
    }

val CardNetwork.iconImageResId: Int
    get() = when (this) {
        CardNetwork.AMEX -> R.drawable.ic_card_amex
        CardNetwork.MASTERCARD -> R.drawable.ic_card_mastercard
        CardNetwork.MAESTRO -> R.drawable.ic_card_maestro
        CardNetwork.VISA -> R.drawable.ic_card_visa
        CardNetwork.DISCOVER -> R.drawable.ic_discover
        CardNetwork.DINERS_CLUB -> R.drawable.ic_diners_club
        CardNetwork.JCB -> R.drawable.ic_jcb
        else -> 0
    }

val CardNetwork.lightIconImageResId: Int
    get() = when (this) {
        CardNetwork.AMEX -> R.drawable.ic_card_amex_light
        CardNetwork.VISA -> R.drawable.ic_card_visa_light
        else -> this.iconImageResId
    }

val CardNetwork.cardNumberMaxLength: Int
    get() = when (this) {
        CardNetwork.AMEX -> 15
        CardNetwork.DINERS_CLUB -> 14
        else -> 16
    }

val CardNetwork.notSupportedErrorMessageResId: Int
    get() = when (this) {
        CardNetwork.VISA -> R.string.error_visa_not_supported
        CardNetwork.MASTERCARD -> R.string.error_mastercard_not_supported
        CardNetwork.MAESTRO -> R.string.error_maestro_not_supported
        CardNetwork.AMEX -> R.string.error_amex_not_supported
        CardNetwork.DISCOVER -> R.string.error_discover_not_supported
        CardNetwork.CHINA_UNION_PAY -> R.string.error_union_pay_not_supported
        CardNetwork.JCB -> R.string.error_jcb_not_supported
        CardNetwork.DINERS_CLUB -> R.string.error_diners_club_not_supported
        else -> R.string.empty
    }

val CardNetwork.defaultCardNameResId: Int
    get() = when (this) {
        CardNetwork.AMEX -> R.string.default_amex_card_title
        CardNetwork.MASTERCARD -> R.string.default_mastercard_card_title
        CardNetwork.MAESTRO -> R.string.default_maestro_card_title
        CardNetwork.VISA -> R.string.default_visa_card_title
        CardNetwork.DISCOVER -> R.string.default_discover_card_title
        CardNetwork.DINERS_CLUB -> R.string.default_dinnersclub_card_title
        CardNetwork.JCB -> R.string.default_jcb_card_title
        CardNetwork.CHINA_UNION_PAY -> R.string.default_chinaunionpay_card_title
        else -> R.string.empty
    }

val CardNetwork.typeId: Int
    get() = when (this) {
        CardNetwork.VISA -> 1
        CardNetwork.MASTERCARD -> 2
        CardNetwork.MAESTRO -> 10
        CardNetwork.AMEX -> 8
        CardNetwork.CHINA_UNION_PAY -> 7
        CardNetwork.JCB -> 9
        CardNetwork.DISCOVER -> 12
        CardNetwork.DINERS_CLUB -> 13
        else -> -1
    }

val CardNetwork.isSupportedByGooglePay: Boolean
    get() = when (this) {
        CardNetwork.VISA,
        CardNetwork.MASTERCARD,
        CardNetwork.AMEX,
        CardNetwork.DISCOVER,
        CardNetwork.MAESTRO,
        CardNetwork.JCB -> true
        else -> false
    }

val CardNetwork?.securityCodeNameOfCardNetwork: String
    get() {
        if (this == null) {
            return CardNetwork.OTHER.securityCodeName
        }
        return securityCodeName
    }

val CardNetwork?.securityCodeNumberMaskOfCardNetwork: String
    get() {
        if (this == null) {
            return CardNetwork.OTHER.securityCodeNumberMask
        }
        return securityCodeNumberMask
    }
