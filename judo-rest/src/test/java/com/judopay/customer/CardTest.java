package com.judopay.customer;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardTest {

    @Test
    public void shouldHaveInvalidLuhn() {
        Card card = new Card("1234567890123456", null, null, new CardDate(1, 99), null);
        assertThat(card.isLuhnValid(), is(false));
    }

    @Test
    public void shouldHaveValidLuhn() {
        Card card = new Card("4976000000003436", null, null, new CardDate(1, 99), null);
        assertThat(card.isLuhnValid(), is(true));
    }

    @Test
    public void shouldHaveValidStartDate() {
        Card card = new Card("4976000000003436", null, new CardDate(1, 12), null, null);
        assertThat(card.isStartDateValid(), is(true));
    }

    @Test
    public void shouldHaveInvalidStartDate() {
        Card card = new Card("4976000000003436", null, new CardDate(1, 99), null, null);
        assertThat(card.isStartDateValid(), is(false));
    }

    @Test
    public void shouldHaveValidExpiryDate() {
        Card card = new Card("4976000000003436", null, null, new CardDate(1, 99), null);
        assertThat(card.isExpiryDateValid(), is(true));
    }

    @Test
    public void shouldMatchAmexCardType() {
        Card card = new Card("340000000000009", null, null, null, null);
        assertThat(card.getType(), is(Card.Type.AMEX));
    }

    @Test
    public void shouldMatchVisaCardType() {
        Card card = new Card("4111111111111111", null, null, null, null);
        assertThat(card.getType(), is(Card.Type.VISA));
    }

    @Test
    public void shouldMatchMastercardCardType() {
        Card card = new Card("5500000000000004", null, null, null, null);
        assertThat(card.getType(), is(Card.Type.MASTERCARD));
    }

    @Test
    public void shouldMatchMaestroCardType() {
        Card card = new Card("6759649826438453", null, null, null, null);
        assertThat(card.getType(), is(Card.Type.MAESTRO));
    }

    @Test
    public void shouldHaveValidCidvAmexCard() {
        Card card = new Card("340000000000009", null, null, null, "1234");

        assertThat(card.isCvvValid(), is(true));
    }

    @Test
    public void shouldHaveInvalidCidvAmexCard() {
        Card card = new Card("340000000000009", null, null, null, "10001");

        assertThat(card.isCvvValid(), is(false));
    }

    @Test
    public void shouldHaveValidCv2VisaCard() {
        Card card = new Card("4111111111111111", null, null, null, "0123");

        assertThat(card.isCvvValid(), is(true));
    }

    @Test
    public void shouldHaveInvalidCv2VisaCard() {
        Card card = new Card("4111111111111111", null, null, null, "1001");

        assertThat(card.isCvvValid(), is(false));
    }

}