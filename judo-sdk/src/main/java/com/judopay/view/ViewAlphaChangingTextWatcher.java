package com.judopay.view;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import static com.judopay.arch.TextUtil.isEmpty;

class ViewAlphaChangingTextWatcher implements View.OnFocusChangeListener {

    private final EditText editText;
    private final View view;

    public ViewAlphaChangingTextWatcher(final @NonNull EditText editText, final @NonNull View view) {
        this.editText = editText;
        this.view = view;
    }

    @Override
    public void onFocusChange(final View v, final boolean hasFocus) {
        if (hasFocus) {
            view.setAlpha(1f);
        } else if (isEmpty(editText.getText())) {
            view.setAlpha(0.5f);
        }
    }
}
