package com.judopay.judokit.android.ui.cardentry.model

import android.content.Context
import com.google.gson.Gson
import com.judopay.judokit.android.ui.common.getLocale

private const val COUNTRIES_JSON_FILE_NAME = "countries.json"
private const val DEFAULT_ALPHA_2_COUNTRY_CODE = "GB"

data class CountryInfo(
    val alpha2Code: String,
    val name: String,
    val dialCode: String,
    val numericCode: String,
    val phoneNumberFormat: String?,
) {
    override fun toString() = name

    companion object {
        private var countryList: Array<CountryInfo>? = null

        fun list(context: Context): Array<CountryInfo> {
            if (countryList == null) {
                countryList =
                    Gson().fromJson(
                        context.assets.open(COUNTRIES_JSON_FILE_NAME).bufferedReader().use { it.readText() },
                        Array<CountryInfo>::class.java,
                    )
            }
            return countryList!!
        }

        fun currentLocaleCountry(
            context: Context,
            default: CountryInfo = list(context).first { it.alpha2Code == DEFAULT_ALPHA_2_COUNTRY_CODE },
        ): CountryInfo {
            val alpha2Code = getLocale(context.resources).country
            return list(context).firstOrNull { it.alpha2Code == alpha2Code } ?: default
        }
    }
}
