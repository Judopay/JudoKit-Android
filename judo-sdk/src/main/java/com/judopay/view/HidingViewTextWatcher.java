package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import static android.text.TextUtils.isEmpty;

/**
 * A TextWatcher that hides the {@link HidingViewTextWatcher#view} when text has been entered.
 */
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
        if(!isEmpty(s) && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
