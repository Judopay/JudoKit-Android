package com.judopay.customer;

import com.judopay.model.Card;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardTest {

    @Test
    public void shouldRequireStartDateAndIssueNumberWhenMaestro() {
        Card card = new Card.Builder()
                .setCardNumber("6759649826438453")
                .build();

        assertThat(card.startDateAndIssueNumberRequired(), is(true));
    }

}