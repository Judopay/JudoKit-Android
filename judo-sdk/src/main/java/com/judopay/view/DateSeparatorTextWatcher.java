package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Watches user input to detect when a card date has been entered and formats it to match
 * the format on a payment card (MM/YY).
 */
class DateSeparatorTextWatcher implements TextWatcher {

    private final EditText editText;

    public DateSeparatorTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (s.length() == 3 && start == 2 && count == 1) {
            removeForwardSlashSilently(s);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable text) {
        if (text.length() == 2 && !text.toString().contains("/")) {
            appendForwardSlashSilently(text);
        }
    }

    private void appendForwardSlashSilently(Editable string) {
        editText.removeTextChangedListener(this);

        string.append("/");

        editText.addTextChangedListener(this);
    }

    private void removeForwardSlashSilently(CharSequence text) {
        editText.removeTextChangedListener(this);

        editText.setText(text.subSequence(0, text.length() - 2));
        editText.setSelection(editText.getText().length());

        editText.addTextChangedListener(this);
    }

}