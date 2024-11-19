package com.judopay.judokit.android.model

import android.os.Parcelable
import com.judopay.judokit.android.R
import com.judopay.judokit.android.model.CardNetwork.Companion.DEFAULT_CARD_NUMBER_MASK
import com.judopay.judokit.android.withWhitespacesRemoved
import kotlinx.parcelize.Parcelize

/**
 * A set of available card networks to choose from when initiating the Judo SDK
 */
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
    OTHER,
    ;

    companion object {
        const val DEFAULT_CARD_NUMBER_MASK = "#### #### #### ####"

        private val REGEX_VISA = "^4\\d{0,15}".toRegex()
        private val REGEX_MASTERCARD =
            "^(5[1-5]\\d{0,2}|22[2-9]\\d{0,1}|2[3-7]\\d{0,2})\\d{0,12}".toRegex()
        private val REGEX_MAESTRO = "^(?:5[0678]\\d{0,2}|6304|67\\d{0,2})\\d{0,12}".toRegex()
        private val REGEX_AMEX = "^3[47]\\d{0,13}".toRegex()
        private val REGEX_DISCOVER = "^(?:6011|65\\d{0,2}|64[4-9]\\d?)\\d{0,12}".toRegex()
        private val REGEX_DINERS_CLUB = "^3(?:0([0-5]|9)|[689]\\d?)\\d{0,11}".toRegex()
        private val REGEX_JCB = "^(?:35\\d{0,2})\\d{0,12}".toRegex()
        private val REGEX_CHINA_UNION_PAY = "^(62|81)\\d{0,14}".toRegex()

        // The card scheme for a card number. This is the name of the card scheme as it appears on the card.
        // As it is returned by the API in the cardDetails object, it is not localized.
        private const val CARD_SCHEME_VISA = "VISA"
        private const val CARD_SCHEME_MASTERCARD = "MASTERCARD"
        private const val CARD_SCHEME_MAESTRO = "MAESTRO"
        private const val CARD_SCHEME_AMEX = "AMEX"
        private const val CARD_SCHEME_CHINA_UNION_PAY = "CHINA UNION PAY"
        private const val CARD_SCHEME_JCB = "JCB"
        private const val CARD_SCHEME_DISCOVER = "DISCOVER"

        /**
         * A method that returns the card network based on the provided card number.
         * @param number - the provided card number
         * @returns one of the pre-defined card network types
         */
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

        /**
         * A method that returns the card network type based on the identifier provided.
         * @param id - The provided card network identifier.
         * @return One of the predefined card network type.
         * https://docs.judopay.com/Content/Developer%20Tools/Codes.htm
         */
        @Suppress("MagicNumber", "CyclomaticComplexMethod")
        fun withIdentifier(
            id: Int,
            scheme: String? = null,
        ): CardNetwork =
            when (id) {
                1, // VISA
                3, // VISA_ELECTRON
                11, // VISA_DEBIT
                13, // VISA_PURCHASING
                -> VISA
                2, // MASTERCARD
                12, // MASTERCARD_DEBIT
                -> MASTERCARD
                10 -> MAESTRO
                8 -> AMEX
                7 -> CHINA_UNION_PAY
                9 -> JCB
                14 -> DISCOVER
                17 -> DINERS_CLUB

                // As a fallback, if the card scheme is provided, use it to determine the card network.
                else -> {
                    when (scheme?.uppercase()) {
                        CARD_SCHEME_VISA -> VISA
                        CARD_SCHEME_MASTERCARD -> MASTERCARD
                        CARD_SCHEME_AMEX -> AMEX
                        CARD_SCHEME_MAESTRO -> MAESTRO
                        CARD_SCHEME_CHINA_UNION_PAY -> CHINA_UNION_PAY
                        CARD_SCHEME_JCB -> JCB
                        CARD_SCHEME_DISCOVER -> DISCOVER
                        else -> OTHER
                    }
                }
            }
    }
}

/**
 * Extension property that returns the mask of the card number based on the card network.
 * @return Mask of the card number.
 */
val CardNetwork.cardNumberMask: String
    get() =
        when (this) {
            CardNetwork.AMEX -> "#### ###### #####"
            CardNetwork.DINERS_CLUB -> "#### ###### ####"
            else -> DEFAULT_CARD_NUMBER_MASK
        }

/**
 * Extension property that returns the mask of the security code based on the card network.
 * @return Mask of the security code.
 */
val CardNetwork.securityCodeNumberMask: String
    get() = if (this == CardNetwork.AMEX) "####" else "###"

/**
 * Extension property that returns security code length based on card network.
 * @return Card network security code length.
 */
@Suppress("MagicNumber")
val CardNetwork.securityCodeLength: Int
    get() = if (this == CardNetwork.AMEX) 4 else 3

/**
 * Extension property that returns security code name based on card network.
 * @return Card network security code name.
 */
val CardNetwork.securityCodeName: String
    get() =
        when (this) {
            CardNetwork.AMEX -> "CID"
            CardNetwork.VISA -> "CVV2"
            CardNetwork.MASTERCARD -> "CVC2"
            CardNetwork.CHINA_UNION_PAY -> "CVN2"
            CardNetwork.JCB -> "CAV2"
            else -> "CVV"
        }

val CardNetwork.securityCodeInvalidResId: Int
    get() =
        when (this) {
            CardNetwork.AMEX -> R.string.jp_check_amex_security_code
            CardNetwork.VISA -> R.string.jp_check_visa_security_code
            CardNetwork.MASTERCARD -> R.string.jp_check_mastercard_security_code
            CardNetwork.CHINA_UNION_PAY -> R.string.jp_check_china_union_pay_security_code
            CardNetwork.JCB -> R.string.jp_check_jcb_security_code
            else -> R.string.jp_check_cvv
        }

/**
 * Extension property that returns the display name of the card network.
 * @return Display name of the card network.
 */
val CardNetwork.displayName: String
    get() =
        when (this) {
            CardNetwork.VISA -> "Visa"
            CardNetwork.MASTERCARD -> "Mastercard"
            CardNetwork.MAESTRO -> "Maestro"
            CardNetwork.AMEX -> "American Express"
            CardNetwork.CHINA_UNION_PAY -> "China UnionPay"
            CardNetwork.JCB -> "JCB"
            CardNetwork.DISCOVER -> "Discover"
            CardNetwork.DINERS_CLUB -> "Diner's Club"
            CardNetwork.OTHER -> "Unknown Card Network"
        }

/**
 * Extension property that returns the icon of the provided card network.
 * @return Drawable resource identifier of the card network icon.
 */
val CardNetwork.iconImageResId: Int
    get() =
        when (this) {
            CardNetwork.AMEX -> R.drawable.ic_card_amex
            CardNetwork.MASTERCARD -> R.drawable.ic_card_mastercard
            CardNetwork.MAESTRO -> R.drawable.ic_card_maestro
            CardNetwork.VISA -> R.drawable.ic_card_visa
            CardNetwork.DISCOVER -> R.drawable.ic_discover
            CardNetwork.DINERS_CLUB -> R.drawable.ic_diners_club
            CardNetwork.JCB -> R.drawable.ic_jcb
            else -> 0
        }

/**
 * Extension property that returns the light version of the icon based on provided card network.
 * @return Drawable resource identifier of the light card network icon.
 */
val CardNetwork.lightIconImageResId: Int
    get() =
        when (this) {
            CardNetwork.AMEX -> R.drawable.ic_card_amex_light
            CardNetwork.VISA -> R.drawable.ic_card_visa_light
            else -> this.iconImageResId
        }

/**
 * Extension property that returns the maximum length of the card number.
 * @return Length of the card number.
 */
@Suppress("MagicNumber")
val CardNetwork.cardNumberMaxLength: Int
    get() =
        when (this) {
            CardNetwork.AMEX -> 15
            CardNetwork.DINERS_CLUB -> 14
            else -> 16
        }

/**
 * Extension property that returns error message of the provided card network.
 * @return The string resource identifier of the error message.
 */
val CardNetwork.notSupportedErrorMessageResId: Int
    get() =
        when (this) {
            CardNetwork.VISA -> R.string.jp_error_visa_not_supported
            CardNetwork.MASTERCARD -> R.string.jp_error_mastercard_not_supported
            CardNetwork.MAESTRO -> R.string.jp_error_maestro_not_supported
            CardNetwork.AMEX -> R.string.jp_error_amex_not_supported
            CardNetwork.DISCOVER -> R.string.jp_error_discover_not_supported
            CardNetwork.CHINA_UNION_PAY -> R.string.jp_error_union_pay_not_supported
            CardNetwork.JCB -> R.string.jp_error_jcb_not_supported
            CardNetwork.DINERS_CLUB -> R.string.jp_error_diners_club_not_supported
            else -> R.string.jp_empty
        }

/**
 * Extension property that returns card name of the provided card network.
 * @return The string resource identifier of the card name.
 */
val CardNetwork.defaultCardNameResId: Int
    get() =
        when (this) {
            CardNetwork.AMEX -> R.string.jp_default_amex_card_title
            CardNetwork.MASTERCARD -> R.string.jp_default_mastercard_card_title
            CardNetwork.MAESTRO -> R.string.jp_default_maestro_card_title
            CardNetwork.VISA -> R.string.jp_default_visa_card_title
            CardNetwork.DISCOVER -> R.string.jp_default_discover_card_title
            CardNetwork.DINERS_CLUB -> R.string.jp_default_dinersclub_card_title
            CardNetwork.JCB -> R.string.jp_default_jcb_card_title
            CardNetwork.CHINA_UNION_PAY -> R.string.jp_default_chinaunionpay_card_title
            else -> R.string.jp_empty
        }

/**
 * Extension property that returns the card network identifier based on the card network provided.
 * @return One of the card network identifiers.
 */
@Suppress("MagicNumber")
val CardNetwork.typeId: Int
    get() =
        when (this) {
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

/**
 * Extension property that checks if provided card network is supported by GooglePay
 * @return True if supported, false if not.
 */
val CardNetwork.isSupportedByGooglePay: Boolean
    get() =
        when (this) {
            CardNetwork.VISA,
            CardNetwork.MASTERCARD,
            CardNetwork.AMEX,
            CardNetwork.DISCOVER,
            CardNetwork.MAESTRO,
            CardNetwork.JCB,
            -> true
            else -> false
        }

/**
 * Extension property that returns security code name of the card network provided.
 * @return Security code name of card network.
 */
val CardNetwork?.securityCodeNameOfCardNetwork: String
    get() {
        if (this == null) {
            return CardNetwork.OTHER.securityCodeName
        }
        return securityCodeName
    }

/**
 * Extension property that returns security code mask of the card network provided.
 * @return Security code mask of card network.
 */
val CardNetwork?.securityCodeNumberMaskOfCardNetwork: String
    get() {
        if (this == null) {
            return CardNetwork.OTHER.securityCodeNumberMask
        }
        return securityCodeNumberMask
    }
