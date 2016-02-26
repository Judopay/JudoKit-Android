package com.judopay.payment.form;

import com.judopay.CardNumberValidation;
import com.judopay.R;
import com.judopay.model.CardType;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LuhnCheckValidationTest {

    @Test
    public void shouldHaveCardNumberEntryCompleteWhenVisa() {
        CardNumberValidation cardNumberValidation = new CardNumberValidation("4282730000002397", CardType.VISA, false, false, false);

        assertThat(cardNumberValidation.isEntryComplete(), is(true));
    }

    @Test
    public void shouldHaveCardNumberEntryCompleteWhenAmex() {
        CardNumberValidation cardNumberValidation = new CardNumberValidation("340000041582957", CardType.AMEX, false, false, true);

        assertThat(cardNumberValidation.isEntryComplete(), is(true));
    }

    @Test
    public void shouldValidateCardNumberAsInvalid() {
        CardNumberValidation cardNumberValidation = new CardNumberValidation("1234567812345678", CardType.UNKNOWN, false, false, true);

        assertThat(cardNumberValidation.isShowError(), is(true));
    }

    @Test
    public void shouldShowErrorWhenMaestroPartiallyEnteredAndMaestroNotSupported() {
        CardNumberValidation cardNumberValidation = new CardNumberValidation("6759", CardType.MAESTRO, false, false, false);
        assertThat(cardNumberValidation.isShowError(), is(true));
    }

    @Test
    public void shouldShowErrorWhenAmexPartiallyEnteredAndAmexNotSupported() {
        CardNumberValidation cardNumberValidation = new CardNumberValidation("3400", CardType.AMEX, false, false, false);
        assertThat(cardNumberValidation.isShowError(), is(true));
    }

    @Test
    public void shouldHaveCardNumberErrorWhenInvalid() {
        CardNumberValidation cardNumberValidation = new CardNumberValidation("1234567812345678", CardType.UNKNOWN, false, false, false);

        assertThat(cardNumberValidation.getError(), is(R.string.check_card_number));
    }

    @Test
    public void shouldHaveCardErrorWhenMaestroEnteredAndMaestroNotSupported() {
        CardNumberValidation cardNumberValidation = new CardNumberValidation("6759649826438453", CardType.MAESTRO, false, false, false);

        assertThat(cardNumberValidation.isShowError(), is(true));
        assertThat(cardNumberValidation.getError(), is(R.string.error_maestro_not_supported));
    }

    @Test
    public void shouldHaveCardErrorWhenAmexEnteredAndAmexNotSupported() {
        CardNumberValidation cardNumberValidation = new CardNumberValidation("340000432128428", CardType.AMEX, false, false, false);

        assertThat(cardNumberValidation.isShowError(), is(true));
        assertThat(cardNumberValidation.getError(), is(R.string.error_amex_not_supported));
    }

}