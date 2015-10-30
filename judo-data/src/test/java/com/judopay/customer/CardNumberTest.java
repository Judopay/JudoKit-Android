package com.judopay.customer;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CardNumberTest {

    @Test
    public void shouldHaveInvalidLuhn() {
        assertThat(CardNumber.isLuhnValid("1234567890123456"), is(false));
    }

    @Test
    public void shouldHaveValidLuhn() {
        assertThat(CardNumber.isLuhnValid("4976000000003436"), is(true));
    }

}