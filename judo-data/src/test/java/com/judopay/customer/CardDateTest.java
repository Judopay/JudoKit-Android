package com.judopay.customer;

import com.judopay.model.CardDate;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class CardDateTest {

    @Test
    public void shouldCreateFromString() {
        CardDate cardDate = new CardDate("12/15");

        assertThat(cardDate.getMonth(), is(12));
        assertThat(cardDate.getYear(), is(15));
    }

    @Test
    public void shouldCreateFromStringZeroPadded() {
        CardDate cardDate = new CardDate("01/01");

        assertThat(cardDate.getMonth(), is(1));
        assertThat(cardDate.getYear(), is(1));
    }

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

    @Test
    public void shouldHaveValidToString() {
        CardDate cardDate = new CardDate(9, 15);
        assertThat(cardDate.toString(), equalTo("09/15"));
    }

}