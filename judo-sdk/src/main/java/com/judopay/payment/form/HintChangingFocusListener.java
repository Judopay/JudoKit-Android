package com.judopay.payment.form;

import android.view.View;
import android.widget.EditText;

public class HintChangingFocusListener implements View.OnFocusChangeListener {

    private final EditText editText;
    private final int hintResourceId;

    public HintChangingFocusListener(EditText editText, int hintResourceId) {
        this.editText = editText;
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
