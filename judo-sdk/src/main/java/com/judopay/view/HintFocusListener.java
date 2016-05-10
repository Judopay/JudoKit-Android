package com.judopay.view;

import android.view.View;
import android.widget.EditText;

public class HintFocusListener implements View.OnFocusChangeListener {

    private final EditText editText;
    private String hint;

    public HintFocusListener(EditText editText, String hint) {
        this.editText = editText;
        this.hint = hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            editText.setHint(hint);
        } else {
            editText.setHint("");
        }
    }
}
