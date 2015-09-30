package com.judopay.customer;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardDateTest {

    @Test
    public void shouldHavePastDate() {
        CardDate cardDate = new CardDate(1, 12);
        assertThat(cardDate.isPastDate(), is(true));
    }

    @Test
    public void shouldHaveFutureDate() {
        CardDate cardDate = new CardDate(1, 99);
        assertThat(cardDate.isPastDate(), is(false));
    }

}