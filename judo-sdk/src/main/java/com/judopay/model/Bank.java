package com.judopay.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.judopay.R;

public enum Bank {
    RABOBANK(R.string.bank_rabo_bic, R.string.bank_rabo, R.drawable.ic_bank_rabobank),
    ABN_AMRO(R.string.bank_abn_amro_bic, R.string.bank_abn_amro, R.drawable.ic_bank_abn_amro),
    VAN_LANSCHOT_BANKIERS(R.string.bank_van_lanschot_bic, R.string.bank_van_lanschot_bankiers, R.drawable.ic_bank_van_lanschot),
    TRIODOS(R.string.bank_triodos_bic, R.string.bank_triodos, R.drawable.ic_bank_triodos),
    ING(R.string.bank_ing_bic, R.string.bank_ing, R.drawable.ic_bank_ing),
    SNS(R.string.bank_sns_bic, R.string.bank_sns, R.drawable.ic_bank_sns),
    ASN(R.string.bank_asn_bic, R.string.bank_asn, R.drawable.ic_bank_asn),
    REGIO(R.string.bank_regio_bic, R.string.bank_regio, R.drawable.ic_bank_regio),
    KNAB(R.string.bank_knab_bic, R.string.bank_knab, R.drawable.ic_bank_knab),
    BUNQ(R.string.bank_bunq_bic, R.string.bank_bunq, R.drawable.ic_bank_bunq),
    MONEYOU(R.string.bank_moneyou_bic, R.string.bank_moneyou, R.drawable.ic_bank_moneyou),
    HANDELSBANKEN(R.string.bank_handelsbanken_bic, R.string.bank_handelsbanken, R.drawable.ic_bank_handelsbanken);

    @StringRes
    private final int bicId;

    @StringRes
    private final int titleResourceId;

    @DrawableRes
    private final int logoResourceId;

    Bank(@StringRes int bicId, @StringRes int titleResourceId, @DrawableRes int logoResourceId) {
        this.bicId = bicId;
        this.titleResourceId = titleResourceId;
        this.logoResourceId = logoResourceId;
    }

    public int getBicId() {
        return bicId;
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public int getLogoResourceId() {
        return logoResourceId;
    }
}