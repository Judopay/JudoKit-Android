package com.judopay.judokit.android.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Testing CardNetwork.withIdentifier lookup")
internal class CardNetworkWithIdentifierTest {
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

    @DisplayName("Given withIdentifier is called, when id is 12, then return MASTERCARD")
    @Test
    fun returnVisaWhenIdIsTwelve() {
        assertEquals(CardNetwork.MASTERCARD, CardNetwork.withIdentifier(12))
    }

    @DisplayName("Given withIdentifier is called, when id is 13, then return VISA")
    @Test
    fun returnVisaWhenIdIsThirteen() {
        assertEquals(CardNetwork.VISA, CardNetwork.withIdentifier(13))
    }

    @DisplayName("Given withIdentifier is called, when id is 0, then return OTHER")
    @Test
    fun returnVisaWhenIdIsZero() {
        assertEquals(CardNetwork.OTHER, CardNetwork.withIdentifier(0))
    }

    @DisplayName("Given withIdentifier is called with unknown id and VISA scheme, then return VISA")
    @Test
    fun withIdentifierFallsBackToVisaScheme() {
        assertEquals(CardNetwork.VISA, CardNetwork.withIdentifier(99, "VISA"))
    }

    @DisplayName("Given withIdentifier is called with unknown id and MASTERCARD scheme, then return MASTERCARD")
    @Test
    fun withIdentifierFallsBackToMastercardScheme() {
        assertEquals(CardNetwork.MASTERCARD, CardNetwork.withIdentifier(99, "MASTERCARD"))
    }

    @DisplayName("Given withIdentifier is called with unknown id and AMEX scheme, then return AMEX")
    @Test
    fun withIdentifierFallsBackToAmexScheme() {
        assertEquals(CardNetwork.AMEX, CardNetwork.withIdentifier(99, "AMEX"))
    }

    @DisplayName("Given withIdentifier is called with unknown id and MAESTRO scheme, then return MAESTRO")
    @Test
    fun withIdentifierFallsBackToMaestroScheme() {
        assertEquals(CardNetwork.MAESTRO, CardNetwork.withIdentifier(99, "MAESTRO"))
    }

    @DisplayName("Given withIdentifier is called with unknown id and CHINA UNION PAY scheme, then return CHINA_UNION_PAY")
    @Test
    fun withIdentifierFallsBackToUnionPayScheme() {
        assertEquals(CardNetwork.CHINA_UNION_PAY, CardNetwork.withIdentifier(99, "CHINA UNION PAY"))
    }

    @DisplayName("Given withIdentifier is called with unknown id and JCB scheme, then return JCB")
    @Test
    fun withIdentifierFallsBackToJcbScheme() {
        assertEquals(CardNetwork.JCB, CardNetwork.withIdentifier(99, "JCB"))
    }

    @DisplayName("Given withIdentifier is called with unknown id and DISCOVER scheme, then return DISCOVER")
    @Test
    fun withIdentifierFallsBackToDiscoverScheme() {
        assertEquals(CardNetwork.DISCOVER, CardNetwork.withIdentifier(99, "DISCOVER"))
    }

    @DisplayName("Given withIdentifier is called with unknown id and null scheme, then return OTHER")
    @Test
    fun withIdentifierReturnsOtherForUnknownIdAndNullScheme() {
        assertEquals(CardNetwork.OTHER, CardNetwork.withIdentifier(99, null))
    }

    @DisplayName("Given withIdentifier is called with unknown id and unrecognised scheme, then return OTHER")
    @Test
    fun withIdentifierReturnsOtherForUnrecognisedScheme() {
        assertEquals(CardNetwork.OTHER, CardNetwork.withIdentifier(99, "UNKNOWN_SCHEME"))
    }

    @DisplayName("Given withIdentifier is called with id 13 (VISA_PURCHASING), then return VISA")
    @Test
    fun withIdentifierReturnsVisaForId13() {
        assertEquals(CardNetwork.VISA, CardNetwork.withIdentifier(13))
    }

    @DisplayName("Given withIdentifier is called with id 12 (MASTERCARD_DEBIT), then return MASTERCARD")
    @Test
    fun withIdentifierReturnsMastercardForId12() {
        assertEquals(CardNetwork.MASTERCARD, CardNetwork.withIdentifier(12))
    }
}
