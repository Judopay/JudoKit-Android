package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class SimpleTextWatcher implements TextWatcher {

    protected abstract void onTextChanged(final CharSequence text);

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        onTextChanged(s);
    }

    @Override
    public void afterTextChanged(final Editable s) {
    }

}
