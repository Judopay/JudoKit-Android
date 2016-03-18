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