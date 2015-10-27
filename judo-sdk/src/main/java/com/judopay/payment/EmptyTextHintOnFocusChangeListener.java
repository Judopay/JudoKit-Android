package com.judopay.payment;

import android.view.View;
import android.widget.TextView;

public class EmptyTextHintOnFocusChangeListener implements View.OnFocusChangeListener {

    private static final int THREE_SECONDS = 3000;

    private final TextView textView;
    private Runnable action;

    public EmptyTextHintOnFocusChangeListener(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onFocusChange(final View view, boolean hasFocus) {
        if (hasFocus) {
            if (((TextView) view).getText().length() == 0) {
                action = new Runnable() {
                    @Override
                    public void run() {
                        if (((TextView) view).getText().length() == 0 && view.hasFocus()) {
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                };
                view.postDelayed(action, THREE_SECONDS);
            }
        } else {
            textView.setVisibility(View.GONE);
            if (action != null) {
                view.removeCallbacks(action);
            }
        }
    }

}