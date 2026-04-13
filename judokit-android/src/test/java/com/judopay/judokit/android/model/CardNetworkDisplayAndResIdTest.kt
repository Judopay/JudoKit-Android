package com.judopay.judokit.android.model

import com.judopay.judokit.android.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing CardNetwork display, icon, and resource ID properties")
internal class CardNetworkDisplayAndResIdTest {
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
            R.string.jp_error_amex_not_supported,
            CardNetwork.AMEX.notSupportedErrorMessageResId,
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is MASTERCARD, return Mastercard error string resource")
    @Test
    fun returnMastercardErrorStringResourceWhenMastercard() {
        assertEquals(
            R.string.jp_error_mastercard_not_supported,
            CardNetwork.MASTERCARD.notSupportedErrorMessageResId,
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is MAESTRO, return Maestro error string resource")
    @Test
    fun returnMaestroErrorStringResourceWhenMaestro() {
        assertEquals(
            R.string.jp_error_maestro_not_supported,
            CardNetwork.MAESTRO.notSupportedErrorMessageResId,
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is VISA, return Visa error string resource")
    @Test
    fun returnVisaErrorStringResourceWhenVisa() {
        assertEquals(
            R.string.jp_error_visa_not_supported,
            CardNetwork.VISA.notSupportedErrorMessageResId,
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is DISCOVER, return Discover error string resource")
    @Test
    fun returnDiscoverErrorStringResourceWhenDiscover() {
        assertEquals(
            R.string.jp_error_discover_not_supported,
            CardNetwork.DISCOVER.notSupportedErrorMessageResId,
        )
    }

    @DisplayName(
        "Given notSupportedErrorMessageResId is called, when card network is DINERS_CLUB, return Diner's Club error string resource",
    )
    @Test
    fun returnDinersClubErrorStringResourceWhenDinersClub() {
        assertEquals(
            R.string.jp_error_diners_club_not_supported,
            CardNetwork.DINERS_CLUB.notSupportedErrorMessageResId,
        )
    }

    @DisplayName("Given notSupportedErrorMessageResId is called, when card network is JCB, return JCB error string resource")
    @Test
    fun returnJCBErrorStringResourceWhenJCB() {
        assertEquals(
            R.string.jp_error_jcb_not_supported,
            CardNetwork.JCB.notSupportedErrorMessageResId,
        )
    }

    @DisplayName(
        "Given notSupportedErrorMessageResId is called, when card network is CHINA_UNION_PAY, return China UnionPay error string resource",
    )
    @Test
    fun returnChinaUnionPayErrorStringResourceWhenChinaUnionPay() {
        assertEquals(
            R.string.jp_error_union_pay_not_supported,
            CardNetwork.CHINA_UNION_PAY.notSupportedErrorMessageResId,
        )
    }

    @DisplayName(
        "Given notSupportedErrorMessageResId is called, when card network is OTHER, return Invalid card number error string resource",
    )
    @Test
    fun returnEmptyStringResourceWhenOther() {
        assertEquals(R.string.jp_check_card_number, CardNetwork.OTHER.notSupportedErrorMessageResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is AMEX, return AmEx error string resource")
    @Test
    fun returnAmexDefaultNameResourceWhenAmex() {
        assertEquals(R.string.jp_default_amex_card_title, CardNetwork.AMEX.defaultCardNameResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is MASTERCARD, return Mastercard error string resource")
    @Test
    fun returnMastercardDefaultNameResourceWhenMastercard() {
        assertEquals(
            R.string.jp_default_mastercard_card_title,
            CardNetwork.MASTERCARD.defaultCardNameResId,
        )
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is MAESTRO, return Maestro error string resource")
    @Test
    fun returnMaestroDefaultNameResourceWhenMaestro() {
        assertEquals(R.string.jp_default_maestro_card_title, CardNetwork.MAESTRO.defaultCardNameResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is VISA, return Visa error string resource")
    @Test
    fun returnVisaDefaultNameResourceWhenVisa() {
        assertEquals(R.string.jp_default_visa_card_title, CardNetwork.VISA.defaultCardNameResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is DISCOVER, return Discover error string resource")
    @Test
    fun returnDiscoverDefaultNameResourceWhenDiscover() {
        assertEquals(
            R.string.jp_default_discover_card_title,
            CardNetwork.DISCOVER.defaultCardNameResId,
        )
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is DINERS_CLUB, return Diner's Club error string resource")
    @Test
    fun returnDinersClubDefaultNameResourceWhenDinersClub() {
        assertEquals(
            R.string.jp_default_dinersclub_card_title,
            CardNetwork.DINERS_CLUB.defaultCardNameResId,
        )
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is JCB, return JCB error string resource")
    @Test
    fun returnJCBErrorDefaultNameWhenJCB() {
        assertEquals(R.string.jp_default_jcb_card_title, CardNetwork.JCB.defaultCardNameResId)
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is CHINA_UNION_PAY, return China UnionPay error string resource")
    @Test
    fun returnChinaUnionPayDefaultNameResourceWhenChinaUnionPay() {
        assertEquals(
            R.string.jp_default_chinaunionpay_card_title,
            CardNetwork.CHINA_UNION_PAY.defaultCardNameResId,
        )
    }

    @DisplayName("Given defaultCardNameResId is called, when card network is OTHER, return empty string resource")
    @Test
    fun returnEmptyStringResourceOnDefaultCardNameResIdWhenOther() {
        assertEquals(R.string.jp_empty, CardNetwork.OTHER.defaultCardNameResId)
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
}
