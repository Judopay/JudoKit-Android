package com.judopay.judokit.android.model.googlepay

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GooglePayPaymentMethodType : Parcelable { CARD, }

@Parcelize
enum class GooglePayAuthMethod : Parcelable { PAN_ONLY, CRYPTOGRAM_3DS }

@Parcelize
enum class GooglePayAddressFormat : Parcelable { MIN, FULL }

@Parcelize
enum class GooglePayTokenizationSpecificationType : Parcelable { PAYMENT_GATEWAY, DIRECT }

@Parcelize
enum class GooglePayPriceStatus : Parcelable { NOT_CURRENTLY_KNOWN, ESTIMATED, FINAL }

@Parcelize
enum class GooglePayCheckoutOption : Parcelable { DEFAULT, COMPLETE_IMMEDIATE_PURCHASE }

@Parcelize
enum class GooglePayEnvironment : Parcelable { PRODUCTION, TEST }
