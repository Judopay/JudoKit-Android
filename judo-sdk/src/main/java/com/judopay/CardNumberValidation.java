package com.judopay;

import com.judopay.model.LuhnCheck;
import com.judopay.model.CardNetwork;

@Deprecated
public class CardNumberValidation {

    private int error;
    private final boolean valid;
    private final boolean showError;
    private final boolean entryComplete;

    public CardNumberValidation(String cardNumber, int cardType, boolean tokenCard, boolean maestroSupported, boolean amexSupported) {
        boolean cardNumberLengthValid = isCardNumberLengthValid(cardNumber, cardType);

        boolean maestroAndNotSupported = isMaestroAndNotSupported(cardType, maestroSupported);

        boolean amexAndNotSupported = isAmexAndNotSupported(cardType, amexSupported);

        this.entryComplete = cardNumberLengthValid;
        this.valid = isCardNumberValid(cardNumber, cardType, tokenCard, maestroSupported, amexSupported) && cardNumberLengthValid;
        this.showError = !valid && (cardNumberLengthValid || maestroAndNotSupported || amexAndNotSupported);

        if (maestroAndNotSupported) {
            this.error = R.string.error_maestro_not_supported;
        } else if (amexAndNotSupported) {
            this.error = R.string.error_amex_not_supported;
        } else if (!valid) {
            this.error = R.string.check_card_number;
        }
    }

    private boolean isAmexAndNotSupported(int cardType, boolean amexSupported) {
        return cardType == CardNetwork.AMEX && !amexSupported;
    }

    private boolean isMaestroAndNotSupported(int cardType, boolean maestroSupported) {
        return cardType == CardNetwork.MAESTRO && !maestroSupported;
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
        return tokenCard || LuhnCheck.isValid(cardNumber)
                && (((cardType != CardNetwork.MAESTRO || maestroSupported))
                && (cardType != CardNetwork.AMEX || amexSupported));
    }

    private boolean isCardNumberLengthValid(String cardNumber, int cardType) {
        switch (cardType) {
            case CardNetwork.AMEX:
                return cardNumber.length() == 15;

            default:
                return cardNumber.length() == 16;
        }
    }

}