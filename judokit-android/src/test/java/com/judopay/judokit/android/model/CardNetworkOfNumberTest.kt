package com.judopay.judokit.android.model

import com.judopay.judokit.android.withWhitespacesRemoved
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing CardNetwork.ofNumber detection")
internal class CardNetworkOfNumberTest {
    @DisplayName("Given ofNumber is called, then call withWhitespacesRemoved")
    @Test
    fun callWithWhitespacesRemovedOnOfNumberCall() {
        mockkStatic("com.judopay.judokit.android.JudoExtensionsKt")
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
}
