package com.judopay;

import android.view.View;
import android.widget.EditText;

public class HintFocusListener implements View.OnFocusChangeListener {

    private final EditText editText;
    private int hintResourceId;

    public HintFocusListener(EditText editText, int hintResourceId) {
        this.editText = editText;
        this.hintResourceId = hintResourceId;
    }

    protected void setHintResourceId(int hintResourceId) {
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
