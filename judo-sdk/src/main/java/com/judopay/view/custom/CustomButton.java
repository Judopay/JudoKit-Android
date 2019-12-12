package com.judopay.view.custom;

import android.graphics.Typeface;

public interface CustomButton {
    void setCustomTextWithFallback(String text, int fallbackText, int fontSize, int textColor, Typeface typeface);
    void setCustomText(String text, int fontSize, int textColor, Typeface typeface);
    void setCustomBackgroundTintList(int light, int dark);
    void setCustomColor(int color);
}
