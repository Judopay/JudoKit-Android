package com.judopay.view;

import android.view.View;
import android.widget.EditText;

public class HintFocusListener implements View.OnFocusChangeListener {

    private final EditText editText;
    private String hint;

    public HintFocusListener(final EditText editText, final String hint) {
        this.editText = editText;
        this.hint = hint;
    }

    public void setHint(final String hint) {
        this.hint = hint;
    }

    @Override
    public void onFocusChange(final View v, final boolean hasFocus) {
        if (hasFocus) {
            editText.setHint(hint);
        } else {
            editText.setHint("");
        }
    }
}
