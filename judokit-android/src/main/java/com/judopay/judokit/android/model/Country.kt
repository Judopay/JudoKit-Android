package com.judopay.judokit.android.model

import com.judopay.judokit.android.R
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_CA
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_OTHER
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_UK
import com.judopay.judokit.android.ui.common.POSTAL_CODE_MAX_LENGTH_USA

// ISO 3166-2 list of supported card form countries
enum class Country {
    GB,
    US,
    CA,
    OTHER
}

fun String.asCountry() = Country.values().firstOrNull { it.displayName.equals(this, true) }

fun Int.asCountry() = Country.values().firstOrNull { it.ISONumericCode == this }

val Country.ISONumericCode: Int?
    get() = when (this) {
        Country.GB -> 826
        Country.US -> 840
        Country.CA -> 124
        Country.OTHER -> null
    }

val Country.displayName: String
    get() = when (this) {
        Country.GB -> "UK"
        Country.US -> "USA"
        Country.CA -> "Canada"
        Country.OTHER -> "Other"
    }

val Country.translatableName: Int
    get() = when (this) {
        Country.GB -> R.string.country_uk
        Country.US -> R.string.country_usa
        Country.CA -> R.string.country_canada
        Country.OTHER -> R.string.country_other
    }

val Country.postcodeMaxLength: Int
    get() = when (this) {
        Country.US -> POSTAL_CODE_MAX_LENGTH_USA
        Country.CA -> POSTAL_CODE_MAX_LENGTH_CA
        Country.GB -> POSTAL_CODE_MAX_LENGTH_UK
        Country.OTHER -> POSTAL_CODE_MAX_LENGTH_OTHER
    }
