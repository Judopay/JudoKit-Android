package com.judopay.payment.form;

import android.view.View;
import android.widget.ScrollView;

class ScrollOnFocusChangeListener implements View.OnFocusChangeListener {

    private final ScrollView scrollView;

    public ScrollOnFocusChangeListener(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            scrollView.smoothScrollTo(0, scrollView.getMaxScrollAmount());
        }
    }

}
