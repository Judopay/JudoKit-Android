package com.judopay.view;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows for multiple {@link android.view.View.OnFocusChangeListener} instances to be registered
 * to a single view, by notifying all listeners when an onFocusChange event occurs.
 */
class MultiOnFocusChangeListener implements View.OnFocusChangeListener {

    private final List<View.OnFocusChangeListener> listeners;

    public MultiOnFocusChangeListener(View.OnFocusChangeListener... listeners) {
        this.listeners = new ArrayList<>(Arrays.asList(listeners));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        for (View.OnFocusChangeListener listener : listeners) {
            listener.onFocusChange(v, hasFocus);
        }
    }

    public void add(View.OnFocusChangeListener listener) {
        this.listeners.add(listener);
    }

}