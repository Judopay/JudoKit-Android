package com.judopay.payment;

import android.support.annotation.StringRes;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.judopay.HintFocusListener;

public class ScrollHintFocusListener extends HintFocusListener {

    private final ScrollView scrollView;

    public ScrollHintFocusListener(EditText editText, ScrollView scrollView, @StringRes int hintResourceId) {
        super(editText, hintResourceId);
        this.scrollView = scrollView;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if(hasFocus) {
            scrollView.smoothScrollTo(0, scrollView.getMaxScrollAmount());
        }
    }

}
