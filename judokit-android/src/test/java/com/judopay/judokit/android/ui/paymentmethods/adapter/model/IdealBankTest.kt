package com.judopay.judokit.android.ui.paymentmethods.adapter.model

import com.judopay.judokit.android.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing IdealBank extension functions")
internal class IdealBankTest {

    @DisplayName("Given bankResId is called, when value is RABOBANK, return RABOBANK string")
    @Test
    fun returnRabobankStringResourceWhenRabobank() {
        assertEquals("Rabobank", IdealBank.RABOBANK.title)
    }

    @DisplayName("Given bankResId is called, when value is ABN_AMRO, return ABN_AMRO string")
    @Test
    fun returnAbnAmroStringResourceWhenAbnAmro() {
        assertEquals("ABN AMRO", IdealBank.ABN_AMRO.title)
    }

    @DisplayName("Given bankResId is called, when value is VAN_LANSCHOT_BANKIERS, return VAN_LANSCHOT_BANKIERS string")
    @Test
    fun returnVanLanschotBankiersStringResourceWhenVanLanschotBankiers() {
        assertEquals("Van Lanschot Bankiers", IdealBank.VAN_LANSCHOT_BANKIERS.title)
    }

    @DisplayName("Given bankResId is called, when value is TRIODOS_BANK, return TRIODOS_BANK string")
    @Test
    fun returnTriodosBankStringResourceWhenTriodosBank() {
        assertEquals("Triodos Bank", IdealBank.TRIODOS_BANK.title)
    }

    @DisplayName("Given bankResId is called, when value is ING_BANK, return ING_BANK string")
    @Test
    fun returnIngBankStringResourceWhenIngBank() {
        assertEquals("ING Bank", IdealBank.ING_BANK.title)
    }

    @DisplayName("Given bankResId is called, when value is SNS_BANK, return SNS_BANK string")
    @Test
    fun returnSnsBankStringResourceWhenSnsBank() {
        assertEquals("SNS Bank", IdealBank.SNS_BANK.title)
    }

    @DisplayName("Given bankResId is called, when value is ASN, return ASN string")
    @Test
    fun returnAsnStringResourceWhenAsn() {
        assertEquals("ASN", IdealBank.ASN.title)
    }

    @DisplayName("Given bankResId is called, when value is REGIOBANK, return REGIOBANK string")
    @Test
    fun returnRegioBankStringResourceWhenRegioBank() {
        assertEquals("RegioBank", IdealBank.REGIOBANK.title)
    }

    @DisplayName("Given bankResId is called, when value is KNAB, return KNAB string")
    @Test
    fun returnKnabStringResourceWhenKnab() {
        assertEquals("Knab", IdealBank.KNAB.title)
    }

    @DisplayName("Given bankResId is called, when value is BUNQ, return BUNQ string")
    @Test
    fun returnBunqStringResourceWhenBunq() {
        assertEquals("Bunq", IdealBank.BUNQ.title)
    }

    @DisplayName("Given bankResId is called, when value is MONEYOU, return MONEYOU string")
    @Test
    fun returnMoneyouStringResourceWhenMoneyou() {
        assertEquals("Moneyou", IdealBank.MONEYOU.title)
    }

    @DisplayName("Given bankResId is called, when value is HANDELSBANKEN, return HANDELSBANKEN string")
    @Test
    fun returnHandelsbankenStringResourceWhenHandelsbanken() {
        assertEquals("Handelsbanken", IdealBank.HANDELSBANKEN.title)
    }

    @DisplayName("Given bic is called, when value is RABOBANK, return RABOBANK Bic string")
    @Test
    fun returnRabobankBicStringResourceWhenRabobank() {
        assertEquals("RABONL2U", IdealBank.RABOBANK.bic)
    }

    @DisplayName("Given bic is called, when value is ABN_AMRO, return ABN_AMRO Bic string")
    @Test
    fun returnAbnAmroBicStringResourceWhenAbnAmro() {
        assertEquals("ABNANL2A", IdealBank.ABN_AMRO.bic)
    }

    @DisplayName("Given bic is called, when value is VAN_LANSCHOT_BANKIERS, return VAN_LANSCHOT_BANKIERS Bic string")
    @Test
    fun returnVanLanschotBankiersBicStringResourceWhenVanLanschotBankiers() {
        assertEquals("FVLBNL22", IdealBank.VAN_LANSCHOT_BANKIERS.bic)
    }

    @DisplayName("Given bic is called, when value is TRIODOS_BANK, return TRIODOS_BANK Bic string")
    @Test
    fun returnTriodosBankBicStringResourceWhenTriodosBank() {
        assertEquals("TRIONL2U", IdealBank.TRIODOS_BANK.bic)
    }

    @DisplayName("Given bic is called, when value is ING_BANK, return ING_BANK Bic string")
    @Test
    fun returnIngBankBicStringResourceWhenIngBank() {
        assertEquals("INGBNL2A", IdealBank.ING_BANK.bic)
    }

    @DisplayName("Given bic is called, when value is SNS_BANK, return SNS_BANK Bic string")
    @Test
    fun returnSnsBankBicStringResourceWhenSnsBank() {
        assertEquals("SNSBNL2A", IdealBank.SNS_BANK.bic)
    }

    @DisplayName("Given bic is called, when value is ASN, return ASN Bic string")
    @Test
    fun returnAsnBicStringResourceWhenAsn() {
        assertEquals("ASNBNL21", IdealBank.ASN.bic)
    }

    @DisplayName("Given bic is called, when value is REGIOBANK, return REGIOBANK Bic string")
    @Test
    fun returnRegioBankBicStringResourceWhenRegioBank() {
        assertEquals("RBRBNL21", IdealBank.REGIOBANK.bic)
    }

    @DisplayName("Given bic is called, when value is KNAB, return KNAB Bic string")
    @Test
    fun returnKnabBicStringResourceWhenKnab() {
        assertEquals("KNABNL2H", IdealBank.KNAB.bic)
    }

    @DisplayName("Given bic is called, when value is BUNQ, return BUNQ Bic string")
    @Test
    fun returnBunqBicStringResourceWhenBunq() {
        assertEquals("BUNQNL2A", IdealBank.BUNQ.bic)
    }

    @DisplayName("Given bic is called, when value is MONEYOU, return MONEYOU Bic string")
    @Test
    fun returnMoneyouBicStringResourceWhenMoneyou() {
        assertEquals("MOYONL21", IdealBank.MONEYOU.bic)
    }

    @DisplayName("Given bic is called, when value is HANDELSBANKEN, return HANDELSBANKEN Bic string resource")
    @Test
    fun returnHandelsbankenBicStringResourceWhenHandelsbanken() {
        assertEquals("HANDNL2A", IdealBank.HANDELSBANKEN.bic)
    }

    @DisplayName("Given drawableResId is called, when value is RABOBANK, return RABOBANK drawable resource")
    @Test
    fun returnRabobankDrawableResourceWhenRabobank() {
        assertEquals(R.drawable.ic_bank_rabobank, IdealBank.RABOBANK.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is ABN_AMRO, return ABN_AMRO drawable resource")
    @Test
    fun returnAbnAmroDrawableResourceWhenAbnAmro() {
        assertEquals(R.drawable.ic_bank_abn_amro, IdealBank.ABN_AMRO.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is VAN_LANSCHOT_BANKIERS, return VAN_LANSCHOT_BANKIERS drawable")
    @Test
    fun returnVanLanschotBankiersDrawableResourceWhenVanLanschotBankiers() {
        assertEquals(R.drawable.ic_bank_van_lanschot, IdealBank.VAN_LANSCHOT_BANKIERS.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is TRIODOS_BANK, return TRIODOS_BANK drawable resource")
    @Test
    fun returnTriodosBankDrawableResourceWhenTriodosBank() {
        assertEquals(R.drawable.ic_bank_triodos, IdealBank.TRIODOS_BANK.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is ING_BANK, return ING_BANK drawable resource")
    @Test
    fun returnIngBankDrawableResourceWhenIngBank() {
        assertEquals(R.drawable.ic_bank_ing, IdealBank.ING_BANK.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is SNS_BANK, return SNS_BANK drawable resource")
    @Test
    fun returnSnsBankDrawableResourceWhenSnsBank() {
        assertEquals(R.drawable.ic_bank_sns, IdealBank.SNS_BANK.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is ASN, return ASN drawable resource")
    @Test
    fun returnAsnDrawableResourceWhenAsn() {
        assertEquals(R.drawable.ic_bank_asn, IdealBank.ASN.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is REGIOBANK, return REGIOBANK drawable resource")
    @Test
    fun returnRegioBankDrawableResourceWhenRegioBank() {
        assertEquals(R.drawable.ic_bank_regio, IdealBank.REGIOBANK.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is KNAB, return KNAB drawable resource")
    @Test
    fun returnKnabDrawableResourceWhenKnab() {
        assertEquals(R.drawable.ic_bank_knab, IdealBank.KNAB.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is BUNQ, return BUNQ drawable resource")
    @Test
    fun returnBunqDrawableResourceWhenBunq() {
        assertEquals(R.drawable.ic_bank_bunq, IdealBank.BUNQ.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is MONEYOU, return MONEYOU drawable resource")
    @Test
    fun returnMoneyouDrawableResourceWhenMoneyou() {
        assertEquals(R.drawable.ic_bank_moneyou, IdealBank.MONEYOU.drawableResId)
    }

    @DisplayName("Given drawableResId is called, when value is HANDELSBANKEN, return HANDELSBANKEN drawable resource")
    @Test
    fun returnHandelsbankenDrawableResourceWhenHandelsbanken() {
        assertEquals(R.drawable.ic_bank_handelsbanken, IdealBank.HANDELSBANKEN.drawableResId)
    }
}
