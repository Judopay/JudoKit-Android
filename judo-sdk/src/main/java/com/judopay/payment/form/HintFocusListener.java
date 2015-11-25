package com.judopay.payment.form;

import android.support.annotation.StringRes;
import android.view.View;
import android.widget.EditText;

public class HintFocusListener implements View.OnFocusChangeListener {

    private final EditText editText;
    private int hintResourceId;

    public HintFocusListener(EditText editText, @StringRes int hintResourceId) {
        this.editText = editText;
        this.hintResourceId = hintResourceId;
    }

    public void setHintResourceId(@StringRes int hintResourceId) {
        this.hintResourceId = hintResourceId;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            editText.setHint(hintResourceId);
        } else {
            editText.setHint("");
        }
    }
}
