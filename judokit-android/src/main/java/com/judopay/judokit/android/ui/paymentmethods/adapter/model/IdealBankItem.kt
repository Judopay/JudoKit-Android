package com.judopay.judokit.android.ui.paymentmethods.adapter.model

import androidx.annotation.DrawableRes
import com.judopay.judokit.android.R

data class IdealBankItem(
    override val type: PaymentMethodItemType = PaymentMethodItemType.IDEAL_BANK_ITEM,
    val idealBank: IdealBank,
    var isSelected: Boolean = false
) : PaymentMethodItem

enum class IdealBank(val title: String, val bic: String, @DrawableRes val drawableResId: Int) {
    ING_BANK("ING Bank", "INGBNL2A", R.drawable.ic_bank_ing),
    ABN_AMRO("ABN AMRO", "ABNANL2A", R.drawable.ic_bank_abn_amro),
    VAN_LANSCHOT_BANKIERS("Van Lanschot Bankiers", "FVLBNL22", R.drawable.ic_bank_van_lanschot),
    TRIODOS_BANK("Triodos Bank", "TRIONL2U", R.drawable.ic_bank_triodos),
    RABOBANK("Rabobank", "RABONL2U", R.drawable.ic_bank_rabobank),
    SNS_BANK("SNS Bank", "SNSBNL2A", R.drawable.ic_bank_sns),
    ASN("ASN", "ASNBNL21", R.drawable.ic_bank_asn),
    REGIOBANK("RegioBank", "RBRBNL21", R.drawable.ic_bank_regio),
    KNAB("Knab", "KNABNL2H", R.drawable.ic_bank_knab),
    BUNQ("Bunq", "BUNQNL2A", R.drawable.ic_bank_bunq),
    MONEYOU("Moneyou", "MOYONL21", R.drawable.ic_bank_moneyou),
    HANDELSBANKEN("Handelsbanken", "HANDNL2A", R.drawable.ic_bank_handelsbanken)
}
