package com.judopay.view;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.judopay.arch.TextUtil;

public class ViewAlphaChangingTextWatcher implements TextWatcher {

    private final View view;

    public ViewAlphaChangingTextWatcher(@NonNull View view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        view.setAlpha(TextUtil.isEmpty(s) ? 0.5f : 1.0f);
    }

}
