package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;

import static java.lang.Character.isDigit;

public class NumberFormatTextWatcher implements TextWatcher {

    private final EditText editText;
    private boolean deleting;
    private int start;

    private boolean formatAhead;

    private String format;

    public NumberFormatTextWatcher(EditText editText, String format) {
        this.editText = editText;
        this.format = format;
    }

    public NumberFormatTextWatcher(EditText editText, String format, boolean formatAhead) {
        this(editText, format);
        this.formatAhead = formatAhead;
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

    public void format(Editable string) {
        if (string.length() > 0) {
            for (int i = string.length(); i > 0; i--) {
                if (!isDigit(string.charAt(i - 1)) || ((deleting && i == start) && (isFormatChar(i)))) {
                    string.delete(i - 1, i);
                }
            }

            for (int i = 0; i < getStringEnd(string); i++) {
                if (isFormatChar(i)) {
                    string.insert(i, String.valueOf(format.charAt(i)));
                }
            }
        }
    }

    private boolean isFormatChar(int index) {
        return index < format.length() && !isDigit(format.charAt(index));
    }

    public int getStringEnd(Editable string) {
        if(formatAhead) {
            return string.length() + 1;
        }
        return string.length();
    }

    public void setFormat(String format) {
        if (!this.format.equals(format)) {
            this.format = format;
            // trigger a key event to reformat the text
            editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
        }
    }

}
