package com.judopay.judokit.android.ui.cardentry.model

import android.content.Context
import com.google.gson.Gson

private const val COUNTRIES_JSON_FILE_NAME = "countries.json"

data class CountryInfo(
    val alpha2Code: String,
    val name: String,
    val dialCode: String,
    val numericCode: String,
    val phoneNumberFormat: String?,
) {
    override fun toString() = name

    companion object {
        fun list(context: Context): Array<CountryInfo> =
            Gson().fromJson(
                context.assets.open(COUNTRIES_JSON_FILE_NAME).bufferedReader().use { it.readText() },
                Array<CountryInfo>::class.java,
            )
    }
}
