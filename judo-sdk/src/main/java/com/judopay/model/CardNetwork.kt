package com.judopay.model

import android.os.Parcelable
import com.judopay.R
import com.judopay.model.CardNetwork.Companion.DEFAULT_CARD_NUMBER_MASK
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
    DINERS_CLUB;

    companion object {
        const val DEFAULT_CARD_NUMBER_MASK = "#### #### #### ####"

        private val REGEX_VISA = "^4[0-9]{3}.*?".toRegex()
        private val REGEX_MASTER_CARD = "^5[1-5][0-9]{2}.*?".toRegex()
        private val REGEX_MAESTRO = "^(5018|5020|5038|6304|6759|6761|6763|6334|6767|4903|4905|4911|4936|5641 82|6331 10|6333|5600|5602|5603|5610|5611|5656|6700|6706|6775|6709|6771|6773).*?".toRegex()
        private val REGEX_AMEX = "^3[47][0-9]{2}.*?".toRegex()
        private val REGEX_DISCOVER = "^65.*?|64[4-9].*?|6011.*?|(622(1 2[6-9].*?|1 [3-9][0-9].*?|[2-8] [0-9][0-9].*?|9 [01][0-9].*?|9 2[0-5].*?).*?)".toRegex()
        private val REGEX_DINERS_CLUB = "^(30[0-5]|309|36|38|39).*?".toRegex()
        private val REGEX_JCB = "^(35[2-8][0-9]).*?".toRegex()
        private val AMEX_PREFIXES = arrayOf("34", "37")
        private val VISA_PREFIXES = arrayOf("4")
        private val MASTER_CARD_PREFIXES = arrayOf("50", "51", "52", "53", "54", "55")

        fun ofNumber(number: String): CardNetwork? {
            return when {
                number.hasOneOfPrefixes(VISA_PREFIXES) || number.matches(REGEX_VISA) -> {
                    VISA
                }

                number.hasOneOfPrefixes(MASTER_CARD_PREFIXES) || number.matches(REGEX_MASTER_CARD) -> {
                    MASTER_CARD
                }

                number.matches(REGEX_MAESTRO) -> {
                    MAESTRO
                }

                number.hasOneOfPrefixes(AMEX_PREFIXES) || number.matches(REGEX_AMEX) -> {
                    AMEX
                }

                number.matches(REGEX_DISCOVER) -> {
                    DISCOVER
                }

                number.matches(REGEX_DINERS_CLUB) -> {
                    DINERS_CLUB
                }

                number.matches(REGEX_JCB) -> {
                    JCB
                }

                else -> {
                    null
                }
            }
        }

        private fun String.hasOneOfPrefixes(prefixes: Array<String>): Boolean {
            for (prefix in prefixes) {
                if (startsWith(prefix)) {
                    return true
                }
            }
            return false
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
        CardNetwork.MASTER_CARD -> "CVC2"
        CardNetwork.CHINA_UNION_PAY -> "CVN2"
        CardNetwork.JCB -> "CAV2"
        else -> "CVV"
    }

val CardNetwork.iconImageResId: Int
    get() = when (this) {
        CardNetwork.AMEX -> R.drawable.ic_card_amex
        CardNetwork.MASTER_CARD -> R.drawable.ic_card_master_card
        CardNetwork.MAESTRO -> R.drawable.ic_card_maestro
        CardNetwork.VISA -> R.drawable.ic_card_visa
        CardNetwork.DISCOVER -> R.drawable.ic_discover
        CardNetwork.DINERS_CLUB -> R.drawable.ic_diners_club
        CardNetwork.JCB -> R.drawable.ic_jcb
        else -> 0
    }


val CardNetwork.cardNumberMaxLength: Int
    get() = when (this) {
        CardNetwork.AMEX -> 15
        CardNetwork.DINERS_CLUB -> 14
        else -> 16
    }

val CardNetwork.notSupportedErrorMessageResId: Int
    get() = when (this) {
        CardNetwork.MAESTRO -> R.string.error_maestro_not_supported
        CardNetwork.AMEX -> R.string.error_amex_not_supported
        CardNetwork.DISCOVER -> R.string.error_discover_not_supported
        CardNetwork.CHINA_UNION_PAY -> R.string.error_union_pay_not_supported
        else -> R.string.empty
    }
