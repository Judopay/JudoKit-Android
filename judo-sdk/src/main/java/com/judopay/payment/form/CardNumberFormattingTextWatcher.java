package com.judopay.payment.form;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class CardNumberFormattingTextWatcher implements TextWatcher {

    private static final char SPACE = ' ';

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        // Remove spacing char
        if (s.length() > 0 && (s.length() % 5) == 0) {
            final char c = s.charAt(s.length() - 1);
            if (SPACE == c) {
                s.delete(s.length() - 1, s.length());
            }
        }
        // Insert char where needed.
        if (s.length() > 0 && (s.length() % 5) == 0) {
            char c = s.charAt(s.length() - 1);
            // Only if its a digit where there should be a SPACE we insert a SPACE
            if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(SPACE)).length <= 3) {
                s.insert(s.length() - 1, String.valueOf(SPACE));
            }
        }
    }

}
