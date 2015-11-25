package com.judopay.payment.form;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class HidingViewTextWatcher implements TextWatcher {

    private final View view;

    public HidingViewTextWatcher(View view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s != null && s.length() > 0 && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
