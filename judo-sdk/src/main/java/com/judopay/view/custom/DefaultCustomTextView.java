package com.judopay.view.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class DefaultCustomTextView extends AppCompatTextView implements CustomTextView {

    public DefaultCustomTextView(Context context) {
        super(context);
    }

    public DefaultCustomTextView(Context context, AttributeSet attrs) {
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
}
