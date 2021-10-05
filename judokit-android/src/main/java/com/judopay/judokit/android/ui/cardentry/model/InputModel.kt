package com.judopay.judokit.android.ui.cardentry.model

import com.judopay.judokit.android.Judo
import com.judopay.judokit.android.model.Country
import com.judopay.judokit.android.model.asCountry
import com.judopay.judokit.android.model.displayName

data class InputModel(
    val cardNumber: String = "",
    val cardHolderName: String = "",
    val expirationDate: String = "",
    val securityNumber: String = "",
    val country: String = Country.GB.displayName,
    val postCode: String = ""
)

fun Judo.toPrePopulatedInputModel(): InputModel {
    var country = Country.GB.displayName
    var postCode = ""

    address?.let {
        val myCountry = it.countryCode?.asCountry() ?: Country.GB
        country = myCountry.displayName
        postCode = it.postCode ?: ""
    }

    return InputModel(country = country, postCode = postCode)
}
