package com.judopay.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

public class DefaultCustomConstraintLayout extends ConstraintLayout implements CustomConstraintLayout {

    public DefaultCustomConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCustomBackgroundColor(int color) {
        if (color != 0) {
            setBackgroundColor(color);
        }
    }
}
