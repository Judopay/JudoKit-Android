package com.judopay.customer;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardTest {

    @Test
    public void shouldHaveValidStartDate() {
        Card card = new Card.Builder()
                .setStartDate(new CardDate(1, 12))
                .build();

        assertThat(card.isStartDateValid(), is(true));
    }

    @Test
    public void shouldHaveInvalidStartDate() {
        Card card = new Card.Builder()
                .setStartDate(new CardDate(1, 99))
                .build();

        assertThat(card.isStartDateValid(), is(false));
    }

    @Test
    public void shouldHaveValidExpiryDate() {
        Card card = new Card.Builder()
                .setExpiryDate(new CardDate(1, 99))
                .build();

        assertThat(card.isExpiryDateValid(), is(true));
    }

    @Test
    public void shouldMatchAmexCardType() {
        Card card = new Card.Builder()
                .setCardNumber("340000000000009")
                .build();

        assertThat(card.getType(), is(CardType.AMEX));
    }

    @Test
    public void shouldMatchVisaCardType() {
        Card card = new Card.Builder()
                .setCardNumber("4111111111111111")
                .build();

        assertThat(card.getType(), is(CardType.VISA));
    }

    @Test
    public void shouldMatchMastercardCardType() {
        Card card = new Card.Builder()
                .setCardNumber("5500000000000004")
                .build();

        assertThat(card.getType(), is(CardType.MASTERCARD));
    }

    @Test
    public void shouldMatchMaestroCardType() {
        Card card = new Card.Builder()
                .setCardNumber("6759649826438453")
                .build();

        assertThat(card.getType(), is(CardType.MAESTRO));
    }

    @Test
    public void shouldHaveValidCidvAmexCard() {
        Card card = new Card.Builder()
                .setCardNumber("340000000000009")
                .setCvv("1234")
                .build();

        assertThat(card.isCvvValid(), is(true));
    }

    @Test
    public void shouldHaveInvalidCidvAmexCard() {
        Card card = new Card.Builder()
                .setCardNumber("340000000000009")
                .setCvv("10001")
                .build();

        assertThat(card.isCvvValid(), is(false));
    }

    @Test
    public void shouldHaveValidCv2VisaCard() {
        Card card = new Card.Builder()
                .setCardNumber("4111111111111111")
                .setCvv("0123")
                .build();

        assertThat(card.isCvvValid(), is(true));
    }

    @Test
    public void shouldHaveInvalidCv2VisaCard() {
        Card card = new Card.Builder()
                .setCardNumber("4111111111111111")
                .setCvv("1001")
                .build();

        assertThat(card.isCvvValid(), is(false));
    }

}