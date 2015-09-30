package com.judopay.customer;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardTest {

    @Test
    public void shouldHaveInvalidLuhn() {
        Card card = new Card("1234567890123456", null, new CardDate(1, 99));
        assertThat(card.isLuhnValid(), is(false));
    }

    @Test
    public void shouldHaveValidLuhn() {
        Card card = new Card("4976000000003436", null, new CardDate(1, 99));
        assertThat(card.isLuhnValid(), is(true));
    }

    @Test
    public void shouldHaveValidStartDate() {
        Card card = new Card("4976000000003436", new CardDate(1, 12), null);
        assertThat(card.isStartDateValid(), is(true));
    }

    @Test
    public void shouldHaveInvalidStartDate() {
        Card card = new Card("4976000000003436", new CardDate(1, 99), null);
        assertThat(card.isStartDateValid(), is(false));
    }

    @Test
    public void shouldHaveValidExpiryDate() {
        Card card = new Card("4976000000003436", null, new CardDate(1, 99));
        assertThat(card.isExpiryDateValid(), is(true));
    }

}