package com.judopay.validation;

import android.widget.EditText;

import com.judopay.R;
import com.judopay.model.CardNetwork;
import com.judopay.model.LuhnCheck;
import com.judopay.view.SimpleTextWatcher;

import io.reactivex.Observable;

public class CardNumberValidator implements Validator {
    private final EditText editText;
    private final boolean maestroSupported;
    private final boolean amexSupported;

    public CardNumberValidator(final EditText editText, final boolean maestroSupported, final boolean amexSupported) {
        this.editText = editText;
        this.maestroSupported = maestroSupported;
        this.amexSupported = amexSupported;
    }

    @Override
    public Observable<Validation> onValidate() {
        return Observable.create(emitter -> editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onTextChanged(final CharSequence text) {
                emitter.onNext(getValidation(text.toString().replaceAll("\\s+", "")));
            }
        }));
    }

    private Validation getValidation(final String cardNumber) {
        final int cardType = CardNetwork.fromCardNumber(cardNumber);

        boolean cardNumberLengthValid = isCardNumberLengthValid(cardNumber, cardType);
        boolean maestroAndNotSupported = isMaestroAndNotSupported(cardType, maestroSupported);
        boolean amexAndNotSupported = isAmexAndNotSupported(cardType, amexSupported);

        boolean valid = isCardNumberValid(cardNumber, cardType) && cardNumberLengthValid;
        boolean showError = !valid && (cardNumberLengthValid || maestroAndNotSupported || amexAndNotSupported);

        int error = 0;
        if (maestroAndNotSupported) {
            error = R.string.error_maestro_not_supported;
        } else if (amexAndNotSupported) {
            error = R.string.error_amex_not_supported;
        } else if (!valid) {
            error = R.string.check_card_number;
        }

        return new Validation(valid, error, showError);
    }

    private boolean isAmexAndNotSupported(final int cardType, final boolean amexSupported) {
        return cardType == CardNetwork.AMEX && !amexSupported;
    }

    private boolean isMaestroAndNotSupported(final int cardType, final boolean maestroSupported) {
        return cardType == CardNetwork.MAESTRO && !maestroSupported;
    }

    private boolean isCardNumberValid(final String cardNumber, final int cardType) {
        return LuhnCheck.isValid(cardNumber)
                && (((cardType != CardNetwork.MAESTRO || maestroSupported))
                && (cardType != CardNetwork.AMEX || amexSupported));
    }

    private boolean isCardNumberLengthValid(final String cardNumber, final int cardType) {
        switch (cardType) {
            case CardNetwork.AMEX:
                return cardNumber.length() == 15;

            default:
                return cardNumber.length() == 16;
        }
    }
}
