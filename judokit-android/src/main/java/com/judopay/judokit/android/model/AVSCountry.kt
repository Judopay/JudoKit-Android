package com.judopay.judokit.android.model

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_CA
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_OTHER
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_UK
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_USA

// ISO 3166-2 list of supported card form countries
enum class AVSCountry {
    GB,
    US,
    CA,
    OTHER,
}

fun String.asAVSCountry() = AVSCountry.values().firstOrNull { it.displayName.equals(this, true) }

fun Int.asAVSCountry() = AVSCountry.values().firstOrNull { it.ISONumericCode == this }

@Suppress("MagicNumber")
val AVSCountry.ISONumericCode: Int?
    get() =
        when (this) {
            AVSCountry.GB -> 826
            AVSCountry.US -> 840
            AVSCountry.CA -> 124
            AVSCountry.OTHER -> null
        }

val AVSCountry.displayName: String
    get() =
        when (this) {
            AVSCountry.GB -> "UK"
            AVSCountry.US -> "USA"
            AVSCountry.CA -> "Canada"
            AVSCountry.OTHER -> "Other"
        }

val AVSCountry.translatableName: Int
    get() =
        when (this) {
            AVSCountry.GB -> R.string.jp_country_uk
            AVSCountry.US -> R.string.jp_country_usa
            AVSCountry.CA -> R.string.jp_country_canada
            AVSCountry.OTHER -> R.string.jp_country_other
        }

val AVSCountry.postcodeMaxLength: Int
    get() =
        when (this) {
            AVSCountry.US -> POSTAL_CODE_MAX_LENGTH_USA
            AVSCountry.CA -> POSTAL_CODE_MAX_LENGTH_CA
            AVSCountry.GB -> POSTAL_CODE_MAX_LENGTH_UK
            AVSCountry.OTHER -> POSTAL_CODE_MAX_LENGTH_OTHER
        }
