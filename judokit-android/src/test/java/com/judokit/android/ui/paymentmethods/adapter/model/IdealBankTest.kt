package com.judokit.android.ui.paymentmethods.adapter.model

import com.judokit.android.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing IdealBank extension functions")
internal class IdealBankTest {

    @DisplayName("Given bankResId is called, when value is RABOBANK, return RABOBANK string resource")
    @Test
    fun returnRabobankStringResourceWhenRabobank() {
        assertEquals(R.string.rabobank, IdealBank.RABOBANK.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is ABN_AMRO, return ABN_AMRO string resource")
    @Test
    fun returnAbnAmroStringResourceWhenAbnAmro() {
        assertEquals(R.string.abn_amro, IdealBank.ABN_AMRO.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is VAN_LANSCHOT_BANKIERS, return VAN_LANSCHOT_BANKIERS string resource")
    @Test
    fun returnVanLanschotBankiersStringResourceWhenVanLanschotBankiers() {
        assertEquals(R.string.van_lanschot_bankiers, IdealBank.VAN_LANSCHOT_BANKIERS.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is TRIODOS_BANK, return TRIODOS_BANK string resource")
    @Test
    fun returnTriodosBankStringResourceWhenTriodosBank() {
        assertEquals(R.string.triodos_bank, IdealBank.TRIODOS_BANK.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is ING_BANK, return ING_BANK string resource")
    @Test
    fun returnIngBankStringResourceWhenIngBank() {
        assertEquals(R.string.ing_bank, IdealBank.ING_BANK.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is SNS_BANK, return SNS_BANK string resource")
    @Test
    fun returnSnsBankStringResourceWhenSnsBank() {
        assertEquals(R.string.sns_bank, IdealBank.SNS_BANK.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is ASN, return ASN string resource")
    @Test
    fun returnAsnStringResourceWhenAsn() {
        assertEquals(R.string.asn, IdealBank.ASN.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is REGIOBANK, return REGIOBANK string resource")
    @Test
    fun returnRegioBankStringResourceWhenRegioBank() {
        assertEquals(R.string.regiobank, IdealBank.REGIOBANK.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is KNAB, return KNAB string resource")
    @Test
    fun returnKnabStringResourceWhenKnab() {
        assertEquals(R.string.knab, IdealBank.KNAB.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is BUNQ, return BUNQ string resource")
    @Test
    fun returnBunqStringResourceWhenBunq() {
        assertEquals(R.string.bunq, IdealBank.BUNQ.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is MONEYOU, return MONEYOU string resource")
    @Test
    fun returnMoneyouStringResourceWhenMoneyou() {
        assertEquals(R.string.moneyou, IdealBank.MONEYOU.bankResId())
    }

    @DisplayName("Given bankResId is called, when value is HANDELSBANKEN, return HANDELSBANKEN string resource")
    @Test
    fun returnHandelsbankenStringResourceWhenHandelsbanken() {
        assertEquals(R.string.handelsbanken, IdealBank.HANDELSBANKEN.bankResId())
    }

    @DisplayName("Given bic is called, when value is RABOBANK, return RABOBANK Bic string resource")
    @Test
    fun returnRabobankBicStringResourceWhenRabobank() {
        assertEquals("RABONL2U", IdealBank.RABOBANK.bic())
    }

    @DisplayName("Given bic is called, when value is ABN_AMRO, return ABN_AMRO Bic string resource")
    @Test
    fun returnAbnAmroBicStringResourceWhenAbnAmro() {
        assertEquals("ABNANL2A", IdealBank.ABN_AMRO.bic())
    }

    @DisplayName("Given bic is called, when value is VAN_LANSCHOT_BANKIERS, return VAN_LANSCHOT_BANKIERS Bic string resource")
    @Test
    fun returnVanLanschotBankiersBicStringResourceWhenVanLanschotBankiers() {
        assertEquals("FVLBNL22", IdealBank.VAN_LANSCHOT_BANKIERS.bic())
    }

    @DisplayName("Given bic is called, when value is TRIODOS_BANK, return TRIODOS_BANK Bic string resource")
    @Test
    fun returnTriodosBankBicStringResourceWhenTriodosBank() {
        assertEquals("TRIONL2U", IdealBank.TRIODOS_BANK.bic())
    }

    @DisplayName("Given bic is called, when value is ING_BANK, return ING_BANK Bic string resource")
    @Test
    fun returnIngBankBicStringResourceWhenIngBank() {
        assertEquals("INGBNL2A", IdealBank.ING_BANK.bic())
    }

    @DisplayName("Given bic is called, when value is SNS_BANK, return SNS_BANK Bic string resource")
    @Test
    fun returnSnsBankBicStringResourceWhenSnsBank() {
        assertEquals("SNSBNL2A", IdealBank.SNS_BANK.bic())
    }

    @DisplayName("Given bic is called, when value is ASN, return ASN Bic string resource")
    @Test
    fun returnAsnBicStringResourceWhenAsn() {
        assertEquals("ASNBNL21", IdealBank.ASN.bic())
    }

    @DisplayName("Given bic is called, when value is REGIOBANK, return REGIOBANK Bic string resource")
    @Test
    fun returnRegioBankBicStringResourceWhenRegioBank() {
        assertEquals("RBRBNL21", IdealBank.REGIOBANK.bic())
    }

    @DisplayName("Given bic is called, when value is KNAB, return KNAB Bic string resource")
    @Test
    fun returnKnabBicStringResourceWhenKnab() {
        assertEquals("KNABNL2H", IdealBank.KNAB.bic())
    }

    @DisplayName("Given bic is called, when value is BUNQ, return BUNQ Bic string resource")
    @Test
    fun returnBunqBicStringResourceWhenBunq() {
        assertEquals("BUNQNL2A", IdealBank.BUNQ.bic())
    }

    @DisplayName("Given bic is called, when value is MONEYOU, return MONEYOU Bic string resource")
    @Test
    fun returnMoneyouBicStringResourceWhenMoneyou() {
        assertEquals("MOYONL21", IdealBank.MONEYOU.bic())
    }

    @DisplayName("Given bic is called, when value is HANDELSBANKEN, return HANDELSBANKEN Bic string resource")
    @Test
    fun returnHandelsbankenBicStringResourceWhenHandelsbanken() {
        assertEquals("HANDNL2A", IdealBank.HANDELSBANKEN.bic())
    }

    @DisplayName("Given drawableResId is called, when value is RABOBANK, return RABOBANK drawable resource")
    @Test
    fun returnRabobankDrawableResourceWhenRabobank() {
        assertEquals(R.drawable.ic_bank_rabobank, IdealBank.RABOBANK.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is ABN_AMRO, return ABN_AMRO drawable resource")
    @Test
    fun returnAbnAmroDrawableResourceWhenAbnAmro() {
        assertEquals(R.drawable.ic_bank_abn_amro, IdealBank.ABN_AMRO.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is VAN_LANSCHOT_BANKIERS, return VAN_LANSCHOT_BANKIERS drawable resource")
    @Test
    fun returnVanLanschotBankiersDrawableResourceWhenVanLanschotBankiers() {
        assertEquals(R.drawable.ic_bank_van_lanschot, IdealBank.VAN_LANSCHOT_BANKIERS.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is TRIODOS_BANK, return TRIODOS_BANK drawable resource")
    @Test
    fun returnTriodosBankDrawableResourceWhenTriodosBank() {
        assertEquals(R.drawable.ic_bank_triodos, IdealBank.TRIODOS_BANK.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is ING_BANK, return ING_BANK drawable resource")
    @Test
    fun returnIngBankDrawableResourceWhenIngBank() {
        assertEquals(R.drawable.ic_bank_ing, IdealBank.ING_BANK.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is SNS_BANK, return SNS_BANK drawable resource")
    @Test
    fun returnSnsBankDrawableResourceWhenSnsBank() {
        assertEquals(R.drawable.ic_bank_sns, IdealBank.SNS_BANK.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is ASN, return ASN drawable resource")
    @Test
    fun returnAsnDrawableResourceWhenAsn() {
        assertEquals(R.drawable.ic_bank_asn, IdealBank.ASN.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is REGIOBANK, return REGIOBANK drawable resource")
    @Test
    fun returnRegioBankDrawableResourceWhenRegioBank() {
        assertEquals(R.drawable.ic_bank_regio, IdealBank.REGIOBANK.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is KNAB, return KNAB drawable resource")
    @Test
    fun returnKnabDrawableResourceWhenKnab() {
        assertEquals(R.drawable.ic_bank_knab, IdealBank.KNAB.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is BUNQ, return BUNQ drawable resource")
    @Test
    fun returnBunqDrawableResourceWhenBunq() {
        assertEquals(R.drawable.ic_bank_bunq, IdealBank.BUNQ.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is MONEYOU, return MONEYOU drawable resource")
    @Test
    fun returnMoneyouDrawableResourceWhenMoneyou() {
        assertEquals(R.drawable.ic_bank_moneyou, IdealBank.MONEYOU.drawableResId())
    }

    @DisplayName("Given drawableResId is called, when value is HANDELSBANKEN, return HANDELSBANKEN drawable resource")
    @Test
    fun returnHandelsbankenDrawableResourceWhenHandelsbanken() {
        assertEquals(R.drawable.ic_bank_handelsbanken, IdealBank.HANDELSBANKEN.drawableResId())
    }
}
