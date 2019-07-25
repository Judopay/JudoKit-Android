package com.judopay.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import static com.judopay.arch.TextUtil.isEmpty;

/**
 * A TextWatcher that hides the {@link HidingViewTextWatcher#view} when text has been entered.
 */
public class HidingViewTextWatcher implements TextWatcher {

    private final View view;

    public HidingViewTextWatcher(final View view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        if(!isEmpty(s) && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(final Editable s) {

    }
}
