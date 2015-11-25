package com.judopay.payment.form;

import com.judopay.R;
import com.judopay.customer.CardNumber;
import com.judopay.customer.CardType;

public class CardNumberValidation {

    private int error;
    private int maxLength;
    private boolean valid;
    private boolean showError;
    private boolean entryComplete;

    public CardNumberValidation(String cardNumber, int cardType, boolean tokenCard, boolean maestroSupported, boolean amexSupported) {
        boolean cardNumberLengthValid = isCardNumberLengthValid(cardNumber, cardType);

        boolean maestroAndNotSupported = isMaestroAndNotSupported(cardType, maestroSupported);

        boolean amexAndNotSupported = isAmexAndNotSupported(cardType, amexSupported);

        this.entryComplete = cardNumberLengthValid;
        this.valid = isCardNumberValid(cardNumber, cardType, tokenCard, maestroSupported, amexSupported) && cardNumberLengthValid;
        this.showError = !valid && (cardNumberLengthValid || maestroAndNotSupported || amexAndNotSupported);
        this.maxLength = getMaxLength(cardType);

        if (maestroAndNotSupported) {
            this.error = R.string.error_maestro_not_supported;
        } else if (amexAndNotSupported) {
            this.error = R.string.error_amex_not_supported;
        } else if (!valid) {
            this.error = R.string.error_card_number;
        }
    }

    private boolean isAmexAndNotSupported(int cardType, boolean amexSupported) {
        return cardType == CardType.AMEX && !amexSupported;
    }

    private boolean isMaestroAndNotSupported(int cardType, boolean maestroSupported) {
        return cardType == CardType.MAESTRO && !maestroSupported;
    }

    private int getMaxLength(int cardType) {
        switch (cardType) {
            case CardType.AMEX:
                return 17;

            default:
                return 19;
        }
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getError() {
        return error;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isShowError() {
        return showError;
    }

    public boolean isEntryComplete() {
        return entryComplete;
    }

    private boolean isCardNumberValid(String cardNumber, int cardType, boolean tokenCard, boolean maestroSupported, boolean amexSupported) {
        return tokenCard || CardNumber.isLuhnValid(cardNumber)
                && (((cardType != CardType.MAESTRO || maestroSupported))
                && (cardType != CardType.AMEX || amexSupported));
    }

    private boolean isCardNumberLengthValid(String cardNumber, int cardType) {
        switch (cardType) {
            case CardType.AMEX:
                return cardNumber.length() == 15;

            default:
                return cardNumber.length() == 16;
        }
    }

}