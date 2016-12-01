package com.judopay.model;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CardTokenTest {

    @Test
    public void shouldFormatEndDate() {
        CardToken token = new CardToken("1220", "1234", "abcdef", 1);

        assertThat(token.getFormattedEndDate(), equalTo("12/20"));
    }

    @Test
    public void shouldReturnEmptyEndDateIfNoEndDate() {
        CardToken token = new CardToken("", "1234", "abcdef", 1);

        assertThat(token.getFormattedEndDate(), equalTo(""));
    }

}