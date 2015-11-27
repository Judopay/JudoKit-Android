package com.judopay.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.judopay.model.CardType;

class CardNumberFormattingTextWatcher implements TextWatcher {

    private static final char SPACE = ' ';

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int cardType = CardType.matchCardNumber(s.toString());
        if (cardType != CardType.AMEX) {
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(SPACE)).length <= 3) {
                    appendSpace(s);
                }
            }
        } else if (s.length() == 5 || s.length() == 12) {
            char c = s.charAt(s.length() - 1);
            if (Character.isDigit(c)) {
                appendSpace(s);
            }
        }
    }

    private void appendSpace(Editable s) {
        s.insert(s.length() - 1, String.valueOf(SPACE));
    }

}
