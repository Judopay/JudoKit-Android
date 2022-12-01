package com.judopay.judokit.android.api.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * The tokenized card data from registering a card, allowing for token payments and token pre-auths
 * to be performed.
 */

@Parcelize
class CardToken(
    @SerializedName("cardLastfour") var lastFour: String? = null,
    @SerializedName("cardToken") var token: String? = null,
    @SerializedName("cardType") var type: Int = 0,
    @SerializedName("cardScheme") var scheme: String? = null,
    @SerializedName("cardFunding") var funding: String? = null,
    @SerializedName("cardCategory") var category: String? = null,
    @SerializedName("cardCountry") var country: String? = null,
    @SerializedName("cardHolderName") var cardHolderName: String? = null,
    var bank: String? = null,
    var endDate: String? = null
) : Parcelable {

    val formattedEndDate: String
        get() {
            val data = endDate ?: ""
            return if (data.length != 4) "" else "${data.substring(0, 2)}/${data.substring(2, 4)}"
        }
}
