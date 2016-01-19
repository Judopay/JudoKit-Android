package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;

import com.judopay.model.CardType;

public class CardNumberFormattingTextWatcher implements TextWatcher {

    private static final String SPACE = " ";
    private static final char SPACE_CHAR = ' ';

    private int start;
    private int before;
    private int count;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.start = start;
        this.before = before;
        this.count = count;
    }

    @Override
    public void afterTextChanged(Editable string) {
        if (string.length() > 0) {
            int cardType = CardType.fromCardNumber(string.toString());

            if (cardType == CardType.AMEX) {
                insertSpaces(string, CardType.AMEX_PATTERN);
            } else {
                insertSpaces(string, CardType.VISA_PATTERN);
            }
        }
    }

    protected void insertSpaces(Editable string, String pattern) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == SPACE_CHAR) {
                if (SPACE_CHAR != pattern.charAt(i)) {
                    if (count == 1 && start == i) { // there is a space in a position where it shouldn't be
                        string.delete(i - 1, i + 1);
                    } else {
                        string.delete(i, i + 1);
                    }
                }
            } else if (SPACE_CHAR == pattern.charAt(i)) {
                // there is a space in the pattern but not in the string
                if(!(before == 1 && count == 0)) {
                    string.insert(i, SPACE);
                }
            }
        }
    }

}
