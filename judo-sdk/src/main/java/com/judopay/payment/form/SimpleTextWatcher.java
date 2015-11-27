package com.judopay.payment.form;

import android.text.Editable;
import android.text.TextWatcher;

abstract class SimpleTextWatcher implements TextWatcher {

    protected abstract void onTextChanged(CharSequence text);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged(s);
    }

    @Override
    public void afterTextChanged(Editable s) { }

}
