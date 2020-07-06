package com.judokit.android.ui.cardentry.model

import com.judokit.android.R
import com.judokit.android.model.CardNetwork
import com.judokit.android.ui.common.ButtonState

data class FormModel(
    val inputModel: InputModel,
    val enabledFields: List<FormFieldType>,
    val supportedNetworks: List<CardNetwork>,
    val paymentButtonState: ButtonState = ButtonState.Disabled(R.string.add_card),
    val cardNetwork: CardNetwork? = null
)

fun FormModel.valueOfFieldWithType(type: FormFieldType): String = with(inputModel) {
    return when (type) {
        FormFieldType.NUMBER -> cardNumber
        FormFieldType.HOLDER_NAME -> cardHolderName
        FormFieldType.EXPIRATION_DATE -> expirationDate
        FormFieldType.SECURITY_NUMBER -> securityNumber
        FormFieldType.COUNTRY -> country
        FormFieldType.POST_CODE -> postCode
    }
}
