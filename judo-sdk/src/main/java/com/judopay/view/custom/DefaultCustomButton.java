package com.judopay.view.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;

public class DefaultCustomButton extends AppCompatButton implements CustomButton {

    public DefaultCustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCustomTextWithFallback(String text, int fallbackText, int fontSize, int textColor) {
        if (text != null) {
            setText(text);
        } else {
            setText(fallbackText);
        }
        if (fontSize != 0) {
            setTextSize(fontSize);
        }
        if (textColor != 0) {
            setTextColor(textColor);
        }
    }

    @Override
    public void setCustomText(String text, int fontSize, int textColor) {
        if (text != null) {
            setText(text);
        }
        if (fontSize != 0) {
            setTextSize(fontSize);
        }
        if (textColor != 0) {
            setTextColor(textColor);
        }
    }

    @Override
    public void setCustomBackgroundTintList(int light, int dark) {
        if (light != 0 || dark != 0) {
            ViewCompat.setBackgroundTintList(this, getColorStateList(light, dark));
        }
    }

    private ColorStateList getColorStateList(int light, int dark) {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{} // this should be empty to make default color as we want
                },
                new int[]{
                        dark,
                        light,
                        dark,
                        light
                }
        );
    }

    @Override
    public void setCustomColor(int color) {
        if (color != 0) {
            setTextColor(color);
        }
    }
}
