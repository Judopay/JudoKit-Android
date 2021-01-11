package com.judokit.android.model

import com.judokit.android.R
import com.judokit.android.withWhitespacesRemoved
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing card network logic")
internal class CardNetworkTest {

    @DisplayName("Given ofNumber is called, then call withWhitespacesRemoved")
    @Test
    fun callWithWhitespacesRemovedOnOfNumberCall() {
        mockkStatic("com.judokit.android.JudoExtensionsKt")
        val number = "4111 1111 1111 1111"

        CardNetwork.ofNumber(number)

        verify { number.withWhitespacesRemoved }
    }

    @DisplayName("Given ofNumber is called, when number is Visa, then return VISA")
    @Test
    fun returnVisaWhenInputIsVisa() {
        val number = "4111 1111 1111 1111"

        assertEquals(CardNetwork.VISA, CardNetwork.ofNumber(number))
    }

    @DisplayName("Given ofNumber is called, when number is Mastercard, then return MASTERCARD")
    @Test
    fun returnMastercardWhenInputIsMastercard() {
        val number = "5500 0000 0000 0004"

        assertEquals(CardNetwork.MASTERCARD, CardNetwork.ofNumber(number))
    }

    @DisplayName("Given ofNumber is called, when number is Maestro, then return MAESTRO")
    @Test
    fun returnMaestroWhenInputIsMaestro() {
        val number = "6759 6498 2643 8453"

        assertEquals(CardNetwork.MAESTRO, CardNetwork.ofNumber(number))
    }

    @DisplayName("Given ofNumber is called, when number is AmEx, then return AMEX")
    @Test
    fun returnAmexWhenInputIsAmex() {
        val number = "3400 0000 0000 009"

        assertEquals(CardNetwork.AMEX, CardNetwork.ofNumber(number))
    }

    @DisplayName("Given ofNumber is called, when number is Discover, then return DISCOVER")
    @Test
    fun returnDiscoverWhenInputIsDiscover() {
        val number = "6011 0000 0000 0004"

        assertEquals(CardNetwork.DISCOVER, CardNetwork.ofNumber(number))
    }

    @DisplayName("Given ofNumber is called, when number is Diner's club, then return DINERS_CLUB")
    @Test
    fun returnDinersClubWhenInputIsDinersClub() {
        val number = "3000 0000 0000 04"

        assertEquals(CardNetwork.DINERS_CLUB, CardNetwork.ofNumber(number))
    }

    @DisplayName("Given ofNumber is called, when number is JCB, then return JCB")
    @Test
    fun returnJCBWhenInputIsJCB() {
        val number = "3569 9900 1009 5841"

        assertEquals(CardNetwork.JCB, CardNetwork.ofNumber(number))
    }

    @DisplayName("Given ofNumber is called, when number is China Union Pay, then return CHINA_UNION_PAY")
    @Test
    fun returnChinaUnionPayWhenInputIsChinaUnionPay() {
        val number = "8171 9999 2766 0000"

        assertEquals(CardNetwork.CHINA_UNION_PAY, CardNetwork.ofNumber(number))
    }

    @DisplayName("Given ofNumber is called, when number is unsupported card, then return OTHER")
    @Test
    fun returnOtherWhenInputIsUnsupportedCard() {
        val number = "4013 2500 0000 0000 006"

        assertEquals(CardNetwork.OTHER, CardNetwork.ofNumber(number))
    }

    @DisplayName("Given withIdentifier is called, when id is 1, then return VISA")
    @Test
    fun returnVisaWhenIdIsOne() {
        assertEquals(CardNetwork.VISA, CardNetwork.withIdentifier(1))
    }

    @DisplayName("Given withIdentifier is called, when id is 3, then return VISA")
    @Test
    fun returnVisaWhenIdIsThree() {
        assertEquals(CardNetwork.VISA, CardNetwork.withIdentifier(3))
    }

    @DisplayName("Given withIdentifier is called, when id is 11, then return VISA")
    @Test
    fun returnVisaWhenIdIsEleven() {
        assertEquals(CardNetwork.VISA, CardNetwork.withIdentifier(11))
    }

    @DisplayName("Given withIdentifier is called, when id is 2, then return MASTERCARD")
    @Test
    fun returnVisaWhenIdIsTwo() {
        assertEquals(CardNetwork.MASTERCARD, CardNetwork.withIdentifier(2))
    }

    @DisplayName("Given withIdentifier is called, when id is 10, then return MAESTRO")
    @Test
    fun returnVisaWhenIdIsTen() {
        assertEquals(CardNetwork.MAESTRO, CardNetwork.withIdentifier(10))
    }

    @DisplayName("Given withIdentifier is called, when id is 8, then return AMEX")
    @Test
    fun returnVisaWhenIdIsEight() {
        assertEquals(CardNetwork.AMEX, CardNetwork.withIdentifier(8))
    }

    @DisplayName("Given withIdentifier is called, when id is 7, then return CHINA_UNION_PAY")
    @Test
    fun returnVisaWhenIdIsSeven() {
        assertEquals(CardNetwork.CHINA_UNION_PAY, CardNetwork.withIdentifier(7))
    }

    @DisplayName("Given withIdentifier is called, when id is 9, then return JCB")
    @Test
    fun returnVisaWhenIdIsNine() {
        assertEquals(CardNetwork.JCB, CardNetwork.withIdentifier(9))
    }

    @DisplayName("Given withIdentifier is called, when id is 12, then return DISCOVER")
    @Test
    fun returnVisaWhenIdIsTwelve() {
        assertEquals(CardNetwork.DISCOVER, CardNetwork.withIdentifier(12))
    }

    @DisplayName("Given withIdentifier is called, when id is 13, then return DINERS_CLUB")
    @Test
    fun returnVisaWhenIdIsThirteen() {
        assertEquals(CardNetwork.DINERS_CLUB, CardNetwork.withIdentifier(13))
    }

    @DisplayName("Given withIdentifier is called, when id is 0, then return OTHER")
    @Test
    fun returnVisaWhenIdIsZero() {
        assertEquals(CardNetwork.OTHER, CardNetwork.withIdentifier(0))
    }

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
        assertEquals(R.string.check_amex_security_code, CardNetwork.AMEX.securityCodeInvalidResId)
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is VISA, return Check your CVV2")
    @Test
    fun returnCheckYourCvv2WhenVisa() {
        assertEquals(R.string.check_visa_security_code, CardNetwork.VISA.securityCodeInvalidResId)
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is MASTERCARD, return Check your CVC2")
    @Test
    fun returnCheckYourCvc2WhenMastercard() {
        assertEquals(
            R.string.check_mastercard_security_code,
            CardNetwork.MASTERCARD.securityCodeInvalidResId
        )
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is CHINA_UNION_PAY, return Check your CVN2")
    @Test
    fun returnCheckYourCvn2WhenChinaUnionPay() {
        assertEquals(
            R.string.check_china_union_pay_security_code,
            CardNetwork.CHINA_UNION_PAY.securityCodeInvalidResId
        )
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is JCB, return Check your CAV2")
    @Test
    fun returnCheckYourCav2WhenJCB() {
        assertEquals(R.string.check_jcb_security_code, CardNetwork.JCB.securityCodeInvalidResId)
    }

    @DisplayName("Given securityCodeInvalidResId is called, when card network is OTHER, return Check your CVV")
    @Test
    fun returnCheckYourCvvWhenOther() {
        assertEquals(R.string.check_cvv, CardNetwork.OTHER.securityCodeInvalidResId)
    }

    @DisplayName("Given displayName is called, when card network is VISA, return Visa")
    @Test
    fun returnVisaDisplayNameWhenVisa() {
        assertEquals("Visa", CardNetwork.VISA.displayName)
    }

    @DisplayName("Given displayName is called, when card network is MASTERCARD, return Mastercard")
    @Test
    fun returnMasterCardDisplayNameWhenMasterCard() {
        assertEquals("Mastercard", CardNetwork.MASTERCARD.displayName)
    }

    @DisplayName("Given displayName is called, when card network is MAESTRO, return Maestro")
    @Test
    fun returnMaestroDisplayNameWhenMasterCard() {
        assertEquals("Maestro", CardNetwork.MAESTRO.displayName)
    }

    @DisplayName("Given displayName is called, when card network is AMEX, return AmEx")
    @Test
    fun returnAmexDisplayNameWhenAmex() {
        assertEquals("American Express", CardNetwork.AMEX.displayName)
    }

    @DisplayName("Given displayName is called, when card network is CHINA_UNION_PAY, return China UnionPay")
    @Test
    fun returnChinaUnionPayDisplayNameWhenChinaUnionPay() {
        assertEquals("China UnionPay", CardNetwork.CHINA_UNION_PAY.displayName)
    }

    @DisplayName("Given displayName is called, when card network is JCB, return JCB")
    @Test
    fun returnJcbDisplayNameWhenJcb() {
        assertEquals("JCB", CardNetwork.JCB.displayName)
    }

    @DisplayName("Given displayName is called, when card network is DISCOVER, return Discover")
    @Test
    fun returnDiscoverDisplayNameWhenDiscover() {
        assertEquals("Discover", CardNetwork.DISCOVER.displayName)
    }

    @DisplayName("Given displayName is called, when card network is DINERS_CLUB, return Diner's Club")
    @Test
    fun returnDinersClubDisplayNameWhenDinersClub() {
        assertEquals("Diner's Club", CardNetwork.DINERS_CLUB.displayName)
    }

    @DisplayName("Given displayName is called, when card network is OTHER, return Unknown Card Network")
    @Test
    fun returnUnknownCardNetworkDisplayNameWhenOther() {
        assertEquals("Unknown Card Network", CardNetwork.OTHER.displayName)
    }

    @DisplayName("Given iconImageResId is called, when card network is AMEX, return AmEx drawable resource")
    @Test
    fun returnAmexDrawableResourceWhenAmex() {
        assertEquals(R.drawable.ic_card_amex, CardNetwork.AMEX.iconImageResId)
    }

    @DisplayName("Given iconImageResId is called, when card network is MASTERCARD, return Mastercard drawable resource")
    @Test
    fun returnMastercardDrawableResourceWhenMastercard() {
        assertEquals(R.drawable.ic_card_mastercard, CardNetwork.MASTERCARD.iconImageResId)
    }

    @DisplayName("Given iconImageResId is called, when card network is MAESTRO, return Maestro drawable resource")
    @Test
    fun returnMaestroDrawableResourceWhenMaestro() {
        assertEquals(R.drawable.ic_card_maestro, CardNetwork.MAESTRO.iconImageResId)
    }

    @DisplayName("Given iconImageResId is called, when card network is VISA, return Visa drawable resource")
    @Test
    fun returnVisaDrawableResourceWhenVisa() {
        assertEquals(R.drawable.ic_card_visa, CardNetwork.VISA.iconImageResId)
    }

    @DisplayName("Given iconImageResId is called, when card network is DISCOVER, return Discover drawable resource")
    @Test
    fun returnDiscoverDrawableResourceWhenDiscover() {
        assertEquals(R.drawable.ic_discover, CardNetwork.DISCOVER.iconImageResId)
    }

    @DisplayName("Given iconImageResId is called, when card network is DINERS_CLUB, return Diner's Club drawable resource")
    @Test
    fun returnDinersClubDrawableResourceWhenDinersClub() {
        assertEquals(R.drawable.ic_diners_club, CardNetwork.DINERS_CLUB.iconImageResId)
    }

    @DisplayName("Given iconImageResId is called, when card network is JCB, return JCB drawable resource")
    @Test
    fun returnJCBDrawableResourceWhenJCB() {
        assertEquals(R.drawable.ic_jcb, CardNetwork.JCB.iconImageResId)
    }

    @DisplayName("Given iconImageResId is called, when card network is OTHER, return 0")
    @Test
    fun returnZeroWhenOther() {
        assertEquals(0, CardNetwork.OTHER.iconImageResId)
    }

    @DisplayName("Given lightIconImageResId is called, when card network is AMEX, return light AmEX drawable resource")
    @Test
    fun returnLightAmexDrawableResourceWhenAmex() {
        assertEquals(R.drawable.ic_card_amex_light, CardNetwork.AMEX.lightIconImageResId)
    }

    @DisplayName("Given lightIconImageResId is called, when card network is VISA, return light Visa drawable resource")
    @Test
    fun returnLightVisaDrawableResourceWhenVisa() {
        assertEquals(R.drawable.ic_card_visa_light, CardNetwork.VISA.lightIconImageResId)
    }

    @DisplayName("Given lightIconImageResId is called, when card network is MASTERCARD, return default Mastercard drawable resource")
    @Test
    fun returnDefaultMastercardDrawableResourceWhenMastercard() {
        assertEquals(R.drawable.ic_card_mastercard, CardNetwork.MASTERCARD.lightIconImageResId)
    }

    @DisplayName("Given cardNumberMaxLength is called, when card network is AMEX, return 15")
    @Test
    fun returnFifteenCardLengthWhenAmex() {
        assertEquals(15, CardNetwork.AMEX.cardNumberMaxLength)
    }

    @DisplayName("Given cardNumberMaxLength is called, when card network is DINERS_CLUB, return 14")
    @Test
    fun returnFourteenCardLengthWhenDinersClub() {
        assertEquals(14, CardNetwork.DINERS_CLUB.cardNumberMaxLength)
    }

    @DisplayName("Given cardNumberMaxLength is called, when card network is VISA, return 16")
    @Test
    fun returnSixteenCardLengthWhenVisa() {
        assertEquals(16, CardNetwork.VISA.cardNumberMaxLength)
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is AMEX, return AmEx error string resource")
    @Test
    fun returnAmexErrorStringResourceWhenAmex() {
        assertEquals(
            R.string.error_amex_not_supported,
            CardNetwork.AMEX.notSupportedErrorMessageResId
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is MASTERCARD, return Mastercard error string resource")
    @Test
    fun returnMastercardErrorStringResourceWhenMastercard() {
        assertEquals(
            R.string.error_mastercard_not_supported,
            CardNetwork.MASTERCARD.notSupportedErrorMessageResId
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is MAESTRO, return Maestro error string resource")
    @Test
    fun returnMaestroErrorStringResourceWhenMaestro() {
        assertEquals(
            R.string.error_maestro_not_supported,
            CardNetwork.MAESTRO.notSupportedErrorMessageResId
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is VISA, return Visa error string resource")
    @Test
    fun returnVisaErrorStringResourceWhenVisa() {
        assertEquals(
            R.string.error_visa_not_supported,
            CardNetwork.VISA.notSupportedErrorMessageResId
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is DISCOVER, return Discover error string resource")
    @Test
    fun returnDiscoverErrorStringResourceWhenDiscover() {
        assertEquals(
            R.string.error_discover_not_supported,
            CardNetwork.DISCOVER.notSupportedErrorMessageResId
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is DINERS_CLUB, return Diner's Club error string resource")
    @Test
    fun returnDinersClubErrorStringResourceWhenDinersClub() {
        assertEquals(
            R.string.error_diners_club_not_supported,
            CardNetwork.DINERS_CLUB.notSupportedErrorMessageResId
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is JCB, return JCB error string resource")
    @Test
    fun returnJCBErrorStringResourceWhenJCB() {
        assertEquals(
            R.string.error_jcb_not_supported,
            CardNetwork.JCB.notSupportedErrorMessageResId
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is CHINA_UNION_PAY, return China UnionPay error string resource")
    @Test
    fun returnChinaUnionPayErrorStringResourceWhenChinaUnionPay() {
        assertEquals(
            R.string.error_union_pay_not_supported,
            CardNetwork.CHINA_UNION_PAY.notSupportedErrorMessageResId
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is OTHER, return empty string resource")
    @Test
    fun returnEmptyStringResourceWhenOther() {
        assertEquals(R.string.empty, CardNetwork.OTHER.notSupportedErrorMessageResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is AMEX, return AmEx error string resource")
    @Test
    fun returnAmexDefaultNameResourceWhenAmex() {
        assertEquals(R.string.default_amex_card_title, CardNetwork.AMEX.defaultCardNameResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is MASTERCARD, return Mastercard error string resource")
    @Test
    fun returnMastercardDefaultNameResourceWhenMastercard() {
        assertEquals(
            R.string.default_mastercard_card_title,
            CardNetwork.MASTERCARD.defaultCardNameResId
        )
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is MAESTRO, return Maestro error string resource")
    @Test
    fun returnMaestroDefaultNameResourceWhenMaestro() {
        assertEquals(R.string.default_maestro_card_title, CardNetwork.MAESTRO.defaultCardNameResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is VISA, return Visa error string resource")
    @Test
    fun returnVisaDefaultNameResourceWhenVisa() {
        assertEquals(R.string.default_visa_card_title, CardNetwork.VISA.defaultCardNameResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is DISCOVER, return Discover error string resource")
    @Test
    fun returnDiscoverDefaultNameResourceWhenDiscover() {
        assertEquals(
            R.string.default_discover_card_title,
            CardNetwork.DISCOVER.defaultCardNameResId
        )
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is DINERS_CLUB, return Diner's Club error string resource")
    @Test
    fun returnDinersClubDefaultNameResourceWhenDinersClub() {
        assertEquals(
            R.string.default_dinnersclub_card_title,
            CardNetwork.DINERS_CLUB.defaultCardNameResId
        )
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is JCB, return JCB error string resource")
    @Test
    fun returnJCBErrorDefaultNameWhenJCB() {
        assertEquals(R.string.default_jcb_card_title, CardNetwork.JCB.defaultCardNameResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is CHINA_UNION_PAY, return China UnionPay error string resource")
    @Test
    fun returnChinaUnionPayDefaultNameResourceWhenChinaUnionPay() {
        assertEquals(
            R.string.default_chinaunionpay_card_title,
            CardNetwork.CHINA_UNION_PAY.defaultCardNameResId
        )
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is OTHER, return empty string resource")
    @Test
    fun returnEmptyStringResourceOnDefaultCardNameResIdWhenOther() {
        assertEquals(R.string.empty, CardNetwork.OTHER.defaultCardNameResId)
    }

    @DisplayName("Given typeId is called, when card network is VISA, return 1")
    @Test
    fun returnOneOnTypeWhenVisa() {
        assertEquals(1, CardNetwork.VISA.typeId)
    }

    @DisplayName("Given typeId is called, when card network is MASTERCARD, return 2")
    @Test
    fun returnTwoOnTypeWhenMastercard() {
        assertEquals(2, CardNetwork.MASTERCARD.typeId)
    }

    @DisplayName("Given typeId is called, when card network is MAESTRO, return 10")
    @Test
    fun returnTenOnTypeWhenMaestro() {
        assertEquals(10, CardNetwork.MAESTRO.typeId)
    }

    @DisplayName("Given typeId is called, when card network is AMEX, return 8")
    @Test
    fun returnEightOnTypeWhenAmex() {
        assertEquals(8, CardNetwork.AMEX.typeId)
    }

    @DisplayName("Given typeId is called, when card network is CHINA_UNION_PAY, return 7")
    @Test
    fun returnSevenOnTypeWhenChinaUnionPay() {
        assertEquals(7, CardNetwork.CHINA_UNION_PAY.typeId)
    }

    @DisplayName("Given typeId is called, when card network is JCB, return 9")
    @Test
    fun returnNineOnTypeWhenJCB() {
        assertEquals(9, CardNetwork.JCB.typeId)
    }

    @DisplayName("Given typeId is called, when card network is DISCOVER, return 12")
    @Test
    fun returnTwelveOnTypeWhenDiscover() {
        assertEquals(12, CardNetwork.DISCOVER.typeId)
    }

    @DisplayName("Given typeId is called, when card network is DINERS_CLUB, return 13")
    @Test
    fun returnTwelveOnTypeWhenDinersClub() {
        assertEquals(13, CardNetwork.DINERS_CLUB.typeId)
    }

    @DisplayName("Given typeId is called, when card network is OTHER, return -1")
    @Test
    fun returnMinusOneOnTypeWhenOther() {
        assertEquals(-1, CardNetwork.OTHER.typeId)
    }

    @DisplayName("Given isSupportedByGooglePay is called, when card network is VISA, return true")
    @Test
    fun returnTrueOnIsSupportedByGooglePayWhenVisa() {
        assertTrue(CardNetwork.VISA.isSupportedByGooglePay)
    }

    @DisplayName("Given isSupportedByGooglePay is called, when card network is MASTERCARD, return true")
    @Test
    fun returnTrueOnIsSupportedByGooglePayWhenMastercard() {
        assertTrue(CardNetwork.MASTERCARD.isSupportedByGooglePay)
    }

    @DisplayName("Given isSupportedByGooglePay is called, when card network is AMEX, return true")
    @Test
    fun returnTrueOnIsSupportedByGooglePayWhenAmex() {
        assertTrue(CardNetwork.AMEX.isSupportedByGooglePay)
    }

    @DisplayName("Given isSupportedByGooglePay is called, when card network is DISCOVER, return true")
    @Test
    fun returnTrueOnIsSupportedByGooglePayWhenDiscover() {
        assertTrue(CardNetwork.DISCOVER.isSupportedByGooglePay)
    }

    @DisplayName("Given isSupportedByGooglePay is called, when card network is MAESTRO, return true")
    @Test
    fun returnTrueOnIsSupportedByGooglePayWhenMaestro() {
        assertTrue(CardNetwork.MAESTRO.isSupportedByGooglePay)
    }

    @DisplayName("Given isSupportedByGooglePay is called, when card network is JCB, return true")
    @Test
    fun returnTrueOnIsSupportedByGooglePayWhenJcb() {
        assertTrue(CardNetwork.JCB.isSupportedByGooglePay)
    }

    @DisplayName("Given isSupportedByGooglePay is called, when card network is CHINA_UNION_PAY, return false")
    @Test
    fun returnFalseOnIsSupportedByGooglePayWhenChinaUnionPay() {
        assertFalse(CardNetwork.CHINA_UNION_PAY.isSupportedByGooglePay)
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
        val network: CardNetwork? = CardNetwork.VISA
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
        val network: CardNetwork? = CardNetwork.AMEX
        assertEquals("####", network.securityCodeNumberMaskOfCardNetwork)
    }
}
