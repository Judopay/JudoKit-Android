package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GooglePayAddress(
    val name: String,
    val postalCode: String,
    val countryCode: String,
    val phoneNumber: String,
    val address1: String?,
    val address2: String?,
    val address3: String?,
    val locality: String?,
    val administrativeArea: String?,
    val sortingCode: String?,
) : Parcelable
