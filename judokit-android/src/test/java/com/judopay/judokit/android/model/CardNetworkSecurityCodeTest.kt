package com.judopay.judokit.android.model

import com.judopay.judokit.android.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing CardNetwork security code properties")
internal class CardNetworkSecurityCodeTest {
    @DisplayName("Given cardNumberMask is called, when card network is AMEX, return AmEx mask")
    @Test
    fun returnAmexMaskOnAmexCardNetwork() {
        assertEquals("#### ###### #####", CardNetwork.AMEX.cardNumberMask)
    }

    @DisplayName("Given cardNumberMask is called, when card network is DINERS_CLUB, return Diner's club mask")
    @Test
    fun returnDinersClubMaskOnDinersClubCardNetwork() {
        assertEquals("#### ###### ####", CardNetwork.DINERS_CLUB.cardNumberMask)
    }

    @DisplayName("Given cardNumberMask is called, when card network is VISA, return default card number mask")
    @Test
    fun returnDefaultCardNumberMaskOnVisaCardNetwork() {
        assertEquals("#### #### #### ####", CardNetwork.VISA.cardNumberMask)
    }

    @DisplayName("Given securityCodeNumberMask is called, when card network is AMEX, return AmEx security code number mask")
    @Test
    fun returnSecurityCodeMaskOnAmexCardNetwork() {
        assertEquals("####", CardNetwork.AMEX.securityCodeNumberMask)
    }

    @DisplayName("Given securityCodeNumberMask is called, when card network is VISA, return default security code number mask")
    @Test
    fun returnSecurityCodeMaskOnVisaCardNetwork() {
        assertEquals("###", CardNetwork.VISA.securityCodeNumberMask)
    }

    @DisplayName("Given securityCodeLength is called, when card network is AMEX, return amex security code length")
    @Test
    fun returnAmexSecurityCodeLengthOnAmexCardNetwork() {
        assertEquals(4, CardNetwork.AMEX.securityCodeLength)
    }

    @DisplayName("Given securityCodeLength is called, when card network is VISA, return default security code length")
    @Test
    fun returnDefaultSecurityCodeLengthOnVisaCardNetwork() {
        assertEquals(3, CardNetwork.VISA.securityCodeLength)
    }

    @DisplayName("Given securityCodeName is called, when card network is AMEX, return CID")
    @Test
    fun returnCidWhenAmex() {
        assertEquals("CID", CardNetwork.AMEX.securityCodeName)
    }

    @DisplayName("Given securityCodeName is called, when card network is VISA, return CVV2")
    @Test
    fun returnCvv2WhenVisa() {
        assertEquals("CVV2", CardNetwork.VISA.securityCodeName)
    }

    @DisplayName("Given securityCodeName is called, when card network is MASTERCARD, return CVC2")
    @Test
    fun returnCvc2WhenMastercard() {
        assertEquals("CVC2", CardNetwork.MASTERCARD.securityCodeName)
    }

    @DisplayName("Given securityCodeName is called, when card network is CHINA_UNION_PAY, return CVN2")
    @Test
    fun returnCvn2WhenChinaUnionPay() {
        assertEquals("CVN2", CardNetwork.CHINA_UNION_PAY.securityCodeName)
    }

    @DisplayName("Given securityCodeName is called, when card network is JCB, return CAV2")
    @Test
    fun returnCav2WhenJCB() {
        assertEquals("CAV2", CardNetwork.JCB.securityCodeName)
    }

    @DisplayName("Given securityCodeName is called, when card network is OTHER, return CVV")
    @Test
    fun returnCvvWhenOther() {
        assertEquals("CVV", CardNetwork.OTHER.securityCodeName)
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is AMEX, return Check your CID")
    @Test
    fun returnCheckYourCidWhenAmex() {
        assertEquals(R.string.jp_check_amex_security_code, CardNetwork.AMEX.securityCodeInvalidResId)
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is VISA, return Check your CVV2")
    @Test
    fun returnCheckYourCvv2WhenVisa() {
        assertEquals(R.string.jp_check_visa_security_code, CardNetwork.VISA.securityCodeInvalidResId)
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is MASTERCARD, return Check your CVC2")
    @Test
    fun returnCheckYourCvc2WhenMastercard() {
        assertEquals(
            R.string.jp_check_mastercard_security_code,
            CardNetwork.MASTERCARD.securityCodeInvalidResId,
        )
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is CHINA_UNION_PAY, return Check your CVN2")
    @Test
    fun returnCheckYourCvn2WhenChinaUnionPay() {
        assertEquals(
            R.string.jp_check_china_union_pay_security_code,
            CardNetwork.CHINA_UNION_PAY.securityCodeInvalidResId,
        )
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is JCB, return Check your CAV2")
    @Test
    fun returnCheckYourCav2WhenJCB() {
        assertEquals(R.string.jp_check_jcb_security_code, CardNetwork.JCB.securityCodeInvalidResId)
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is OTHER, return Check your CVV")
    @Test
    fun returnCheckYourCvvWhenOther() {
        assertEquals(R.string.jp_check_cvv, CardNetwork.OTHER.securityCodeInvalidResId)
    }

    @DisplayName("Given securityCodeNameOfCardNetwork is called, when card network is null, return CVV")
    @Test
    fun returnCVVOnSecurityCodeNameOfCardNetworkWhenCardNetworkNull() {
        val network: CardNetwork? = null
        assertEquals("CVV", network.securityCodeNameOfCardNetwork)
    }

    @DisplayName("Given securityCodeNameOfCardNetwork is called, when card network is VISA, return CV2")
    @Test
    fun returnCV2OnSecurityCodeNameOfCardNetworkWhenCardNetworkVisa() {
        val network = CardNetwork.VISA
        assertEquals("CVV2", network.securityCodeNameOfCardNetwork)
    }

    @DisplayName("Given securityCodeNumberMaskOfCardNetwork is called, when card network is null, return default mask")
    @Test
    fun returnDefaultMaskOnsecurityCodeNumberMaskOfCardNetworkWhenCardNetworkNull() {
        val network: CardNetwork? = null
        assertEquals("###", network.securityCodeNumberMaskOfCardNetwork)
    }

    @DisplayName("Given securityCodeNumberMaskOfCardNetwork is called, when card network is AMEX, return ####")
    @Test
    fun returnAmexMaskOnSecurityCodeNameOfCardNetworkWhenCardNetworkAmex() {
        val network = CardNetwork.AMEX
        assertEquals("####", network.securityCodeNumberMaskOfCardNetwork)
    }
}
