package com.judopay.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CardDateTest {

    @Test
    public void shouldHandleDateWithoutForwardSlashSeparator() {
        CardDate cardDate = new CardDate("1212");
        assertThat(cardDate.isBeforeToday(), is(true));
    }

    @Test
    public void dateShouldBeBeforeToday() {
        CardDate cardDate = new CardDate("12/12");
        assertThat(cardDate.isBeforeToday(), is(true));
    }

    @Test
    public void dateShouldBeAfterToday() {
        CardDate cardDate = new CardDate("12/30");
        assertThat(cardDate.isAfterToday(), is(true));
    }

    @Test
    public void dateShouldBeOutsideAllowedRangeWhenOlderThanTenYearsAgo() {
        CardDate cardDate = new CardDate("12/05");
        assertThat(cardDate.isInsideAllowedDateRange(), is(false));
    }

    @Test
    public void dateShouldBeOutsideAllowedRangeWhenMoreThanTenYearsInFuture() {
        CardDate cardDate = new CardDate("12/30");
        assertThat(cardDate.isInsideAllowedDateRange(), is(false));
    }

}