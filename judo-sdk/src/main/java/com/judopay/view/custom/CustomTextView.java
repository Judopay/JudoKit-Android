package com.judopay.view.custom;

import android.graphics.Typeface;

public interface CustomTextView {
    void setCustomTextWithFallback(String text, int fallbackText, int fontSize, int textColor, Typeface typeface);
    void setCustomText(String text, int fontSize, int textColor, Typeface typeface);
}
