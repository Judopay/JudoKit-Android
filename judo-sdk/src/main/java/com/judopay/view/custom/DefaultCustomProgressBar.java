package com.judopay.view.custom;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class DefaultCustomProgressBar extends ProgressBar implements CustomProgressBar {

    public DefaultCustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCustomColor(int color) {
        if (color != 0) {
            getIndeterminateDrawable().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
    }
}
