package com.judopay;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeOnFocusChangeListener implements View.OnFocusChangeListener {

    private final List<View.OnFocusChangeListener> listeners;

    public CompositeOnFocusChangeListener(View.OnFocusChangeListener... listeners) {
        this.listeners = new ArrayList<>(Arrays.asList(listeners));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        for (View.OnFocusChangeListener listener : listeners) {
            listener.onFocusChange(v, hasFocus);
        }
    }

}
