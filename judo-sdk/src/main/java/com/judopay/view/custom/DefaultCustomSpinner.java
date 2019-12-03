package com.judopay.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSpinner;

public class DefaultCustomSpinner extends AppCompatSpinner implements CustomSpinner {

    public DefaultCustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCustomBackgroundColor(int color) {
        setBackgroundColor(color);
    }
}
