package com.judopay.view;

import android.view.View;
import android.widget.ScrollView;

class ScrollOnFocusChangeListener implements View.OnFocusChangeListener {

    private final ScrollView scrollView;

    public ScrollOnFocusChangeListener(final ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public void onFocusChange(final View v, final boolean hasFocus) {
        if (hasFocus) {
            scrollView.smoothScrollTo(0, scrollView.getMaxScrollAmount());
        }
    }

}
