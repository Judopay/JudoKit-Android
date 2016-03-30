package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;

public class NumberFormatTextWatcher implements TextWatcher {

    private final EditText editText;
    private boolean deleting;
    private int start;

    private String format;

    public NumberFormatTextWatcher(EditText editText, String format) {
        this.editText = editText;
        this.format = format;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        deleting = before == 1;
        this.start = start;
    }

    @Override
    public void afterTextChanged(Editable string) {
        editText.removeTextChangedListener(this);
        format(string);
        editText.addTextChangedListener(this);
    }

    public void format(Editable s) {
        for (int i = s.length(); i > 0; i--) {
            if (s.charAt(i - 1) == ' ' || ((deleting && i == start) && format.charAt(i) == ' ')) {
                s.delete(i - 1, i);
            }
        }

        for (int i = 0; i < s.length(); i++) {
            if (i < format.length() && format.charAt(i) == ' ') {
                s.insert(i, " ");
            }
        }
    }

    public void setFormat(String format) {
        if (!this.format.equals(format)) {
            this.format = format;
            // trigger a key event to reformat the text
            editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
        }
    }

}
