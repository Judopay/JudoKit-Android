package com.judopay.view;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PaddedNumberFormatterTest {

    @Test
    public void shouldHandleSingleCharacter() {
        String result = PaddedNumberFormatter.format("1", "0");
        assertThat(result, equalTo("1"));
    }

    @Test
    public void shouldFormatNumberShorterThanFormat() {
        String result = PaddedNumberFormatter.format("1", "00");
        assertThat(result, equalTo("1"));
    }

    @Test
    public void shouldFormatNumberSingleSpace() {
        String result = PaddedNumberFormatter.format("1234", "00 00");
        assertThat(result, equalTo("12 34"));
    }

    @Test
    public void shouldFormatNumberMultipleSpaces() {
        String result = PaddedNumberFormatter.format("1234", "0 0 0 0");
        assertThat(result, equalTo("1 2 3 4"));
    }

    @Test
    public void shouldFormatNumberWithNoSpaces() {
        String result = PaddedNumberFormatter.format("1234", "0000");
        assertThat(result, equalTo("1234"));
    }

    @Test
    public void shouldFormatMultipleNumbersWithMultipleSpaces() {
        String result = PaddedNumberFormatter.format("123412341234", "0000 0000 0000");
        assertThat(result, equalTo("1234 1234 1234"));
    }

}