package com.judopay.judokit.android.model

data class State(val isoCode: String, val name: String) {
    override fun toString() = this.name
}

val usStates =
    listOf(
        State(isoCode = "AK", name = "Alaska"),
        State(isoCode = "AZ", name = "Arizona"),
        State(isoCode = "AR", name = "Arkansas"),
        State(isoCode = "CA", name = "California"),
        State(isoCode = "CO", name = "Colorado"),
        State(isoCode = "CT", name = "Connecticut"),
        State(isoCode = "DE", name = "Delaware"),
        State(isoCode = "AL", name = "Alabama"),
        State(isoCode = "DC", name = "District Of Columbia"),
        State(isoCode = "FL", name = "Florida"),
        State(isoCode = "GA", name = "Georgia"),
        State(isoCode = "HI", name = "Hawaii"),
        State(isoCode = "ID", name = "Idaho"),
        State(isoCode = "IL", name = "Illinois"),
        State(isoCode = "IN", name = "Indiana"),
        State(isoCode = "IA", name = "Iowa"),
        State(isoCode = "KS", name = "Kansas"),
        State(isoCode = "KY", name = "Kentucky"),
        State(isoCode = "LA", name = "Louisiana"),
        State(isoCode = "ME", name = "Maine"),
        State(isoCode = "MD", name = "Maryland"),
        State(isoCode = "MA", name = "Massachusetts"),
        State(isoCode = "MI", name = "Michigan"),
        State(isoCode = "MN", name = "Minnesota"),
        State(isoCode = "MS", name = "Mississippi"),
        State(isoCode = "MO", name = "Missouri"),
        State(isoCode = "MT", name = "Montana"),
        State(isoCode = "NE", name = "Nebraska"),
        State(isoCode = "NV", name = "Nevada"),
        State(isoCode = "NH", name = "New Hampshire"),
        State(isoCode = "NJ", name = "New Jersey"),
        State(isoCode = "NM", name = "New Mexico"),
        State(isoCode = "NY", name = "New York"),
        State(isoCode = "NC", name = "North Carolina"),
        State(isoCode = "ND", name = "North Dakota"),
        State(isoCode = "OH", name = "Ohio"),
        State(isoCode = "OK", name = "Oklahoma"),
        State(isoCode = "OR", name = "Oregon"),
        State(isoCode = "PA", name = "Pennsylvania"),
        State(isoCode = "RI", name = "Rhode Island"),
        State(isoCode = "SC", name = "South Carolina"),
        State(isoCode = "SD", name = "South Dakota"),
        State(isoCode = "TN", name = "Tennessee"),
        State(isoCode = "TX", name = "Texas"),
        State(isoCode = "UT", name = "Utah"),
        State(isoCode = "VT", name = "Vermont"),
        State(isoCode = "VA", name = "Virginia"),
        State(isoCode = "WA", name = "Washington"),
        State(isoCode = "WV", name = "West Virginia"),
        State(isoCode = "WI", name = "Wisconsin"),
        State(isoCode = "WY", name = "Wyoming"),
    )

val canadaProvincesAndTerritories =
    listOf(
        State(isoCode = "AB", name = "Alberta"),
        State(isoCode = "BC", name = "British Columbia"),
        State(isoCode = "MB", name = "Manitoba"),
        State(isoCode = "NB", name = "New Brunswick"),
        State(isoCode = "NL", name = "Newfoundland and Labrador"),
        State(isoCode = "NS", name = "Nova Scotia"),
        State(isoCode = "NT", name = "Northwest Territories"),
        State(isoCode = "NU", name = "Nunavut"),
        State(isoCode = "ON", name = "Ontario"),
        State(isoCode = "PE", name = "Prince Edward Island"),
        State(isoCode = "QC", name = "Quebec"),
        State(isoCode = "SK", name = "Saskatchewan"),
        State(isoCode = "YT", name = "Yukon"),
    )

val indiaStates =
    listOf(
        // Todo
        State(isoCode = "IN1", name = "Fake State 1"),
        State(isoCode = "IN2", name = "Fake State 2"),
    )
