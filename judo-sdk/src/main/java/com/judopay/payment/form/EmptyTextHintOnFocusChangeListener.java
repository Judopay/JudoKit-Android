package com.judopay.payment.form;

import android.view.View;
import android.widget.TextView;

class EmptyTextHintOnFocusChangeListener implements View.OnFocusChangeListener {

    private static final int THREE_SECONDS = 3000;

    private final View hintView;
    private Runnable action;

    public EmptyTextHintOnFocusChangeListener(View hintView) {
        this.hintView = hintView;
    }

    @Override
    public void onFocusChange(final View view, boolean hasFocus) {
        if (hasFocus) {
            if (((TextView) view).getText().length() == 0) {
                action = new Runnable() {
                    @Override
                    public void run() {
                        if (((TextView) view).getText().length() == 0 && view.hasFocus()) {
                            EmptyTextHintOnFocusChangeListener.this.hintView.setVisibility(View.VISIBLE);
                        }
                    }
                };
                view.postDelayed(action, THREE_SECONDS);
            }
        } else {
            this.hintView.setVisibility(View.GONE);
            if (action != null) {
                view.removeCallbacks(action);
            }
        }
    }

}