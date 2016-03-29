package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class NumberFormatTextWatcher implements TextWatcher {

    private final EditText editText;
    private static final String FORMAT = "0000 0000 0000 0000";
    private boolean deleting;
    private int start;

    public NumberFormatTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        System.out.print("" + start + count + after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        System.out.print("" + start + before + count);
        deleting = before == 1;
        this.start = start;
    }

    @Override
    public void afterTextChanged(Editable s) {
       editText.removeTextChangedListener(this);

        for (int i = s.length(); i > 0; i--) {
            if (s.charAt(i - 1) == ' ' || ((deleting && i == start) && FORMAT.charAt(i) == ' ')) {
                s.delete(i - 1, i);
            }
        }

        for (int i = 0; i < s.length(); i++) {
            if (i < FORMAT.length() && FORMAT.charAt(i) == ' ') {
                s.insert(i, " ");
            }
        }

        editText.addTextChangedListener(this);
    }
}
