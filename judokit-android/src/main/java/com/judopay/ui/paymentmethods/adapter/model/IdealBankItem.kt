package com.judopay.ui.paymentmethods.adapter.model

import com.judopay.R

data class IdealBankItem(
    override val type: PaymentMethodItemType = PaymentMethodItemType.IDEAL_BANK_ITEM,
    val idealBank: IdealBank,
    var isSelected: Boolean = false
) : PaymentMethodItem

enum class IdealBank {
    ING_BANK,
    ABN_AMRO,
    VAN_LANSCHOT_BANKIERS,
    TRIODOS_BANK,
    RABOBANK,
    SNS_BANK,
    ASN,
    REGIOBANK,
    KNAB,
    BUNQ,
    MONEYOU,
    HANDELSBANKEN
}

fun IdealBank.bankResId(): Int = when (this) {
    IdealBank.RABOBANK -> R.string.rabobank
    IdealBank.ABN_AMRO -> R.string.abn_amro
    IdealBank.VAN_LANSCHOT_BANKIERS -> R.string.van_lanschot_bankiers
    IdealBank.TRIODOS_BANK -> R.string.triodos_bank
    IdealBank.ING_BANK -> R.string.ing_bank
    IdealBank.SNS_BANK -> R.string.sns_bank
    IdealBank.ASN -> R.string.asn
    IdealBank.REGIOBANK -> R.string.regiobank
    IdealBank.KNAB -> R.string.knab
    IdealBank.BUNQ -> R.string.bunq
    IdealBank.MONEYOU -> R.string.moneyou
    IdealBank.HANDELSBANKEN -> R.string.handelsbanken
}

fun IdealBank.bic(): String = when (this) {
    IdealBank.RABOBANK -> "RABONL2U"
    IdealBank.ABN_AMRO -> "ABNANL2A"
    IdealBank.VAN_LANSCHOT_BANKIERS -> "FVLBNL22"
    IdealBank.TRIODOS_BANK -> "TRIONL2U"
    IdealBank.ING_BANK -> "INGBNL2A"
    IdealBank.SNS_BANK -> "SNSBNL2A"
    IdealBank.ASN -> "ASNBNL21"
    IdealBank.REGIOBANK -> "RBRBNL21"
    IdealBank.KNAB -> "KNABNL2H"
    IdealBank.BUNQ -> "BUNQNL2A"
    IdealBank.MONEYOU -> "MOYONL21"
    IdealBank.HANDELSBANKEN -> "HANDNL2A"
}

fun IdealBank.drawableResId(): Int = when (this) {
    IdealBank.RABOBANK -> R.drawable.ic_bank_rabobank
    IdealBank.ABN_AMRO -> R.drawable.ic_bank_abn_amro
    IdealBank.VAN_LANSCHOT_BANKIERS -> R.drawable.ic_bank_van_lanschot
    IdealBank.TRIODOS_BANK -> R.drawable.ic_bank_triodos
    IdealBank.ING_BANK -> R.drawable.ic_bank_ing
    IdealBank.SNS_BANK -> R.drawable.ic_bank_sns
    IdealBank.ASN -> R.drawable.ic_bank_asn
    IdealBank.REGIOBANK -> R.drawable.ic_bank_regio
    IdealBank.KNAB -> R.drawable.ic_bank_knab
    IdealBank.BUNQ -> R.drawable.ic_bank_bunq
    IdealBank.MONEYOU -> R.drawable.ic_bank_moneyou
    IdealBank.HANDELSBANKEN -> R.drawable.ic_bank_handelsbanken
}
