package com.judopay.payment.form.cardnumber;

import com.judopay.R;
import com.judopay.customer.CardNumber;
import com.judopay.customer.CardType;

public class CardNumberValidation {

    private int error;
    private int maxLength;
    private boolean valid;
    private boolean showError;
    private boolean entryComplete;

    public CardNumberValidation(String cardNumber, int cardType, boolean maestroSupported, boolean amexSupported) {
        boolean cardNumberLengthValid = isCardNumberLengthValid(cardNumber, cardType);

        this.entryComplete = cardNumberLengthValid;
        this.valid = isCardNumberValid(cardNumber, cardType, maestroSupported, amexSupported) && cardNumberLengthValid;
        this.showError = !valid && cardNumberLengthValid;
        this.maxLength = getMaxLength(cardType);

        boolean maestroError = cardType == CardType.MAESTRO && !maestroSupported;
        boolean amexError = cardType == CardType.AMEX && !amexSupported;

        if (maestroError) {
            this.error = R.string.error_maestro_not_supported;
        } else if (amexError) {
            this.error = R.string.error_amex_not_supported;
        } else if (!valid) {
            this.error = R.string.error_card_number;
        }
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

    private boolean isCardNumberValid(String cardNumber, int cardType, boolean maestroSupported, boolean amexSupported) {
        return CardNumber.isLuhnValid(cardNumber)
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