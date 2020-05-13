package com.judopay.model

// ISO 3166-2 list of supported card form countries
enum class Country {
    GB,
    US,
    CA,
    OTHER
}

fun String.asCountry() = Country.values().firstOrNull { it.displayName.equals(this, true) }

val Country.displayName: String
    get() = when (this) {
        Country.GB -> "UK"
        Country.US -> "USA"
        Country.CA -> "Canada"
        Country.OTHER -> "Other"
    }

val Country.postcodeMaxLength: Int
    get() = when (this) {
        Country.US -> 5
        Country.CA -> 6
        Country.GB,
        Country.OTHER -> 8
    }
