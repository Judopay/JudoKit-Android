package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {

    protected abstract void onTextChanged();

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged();
    }

    @Override
    public void afterTextChanged(Editable s) { }

}
