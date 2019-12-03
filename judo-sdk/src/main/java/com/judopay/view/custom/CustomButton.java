package com.judopay.view.custom;

public interface CustomButton {
    void setCustomTextWithFallback(String text, int fallbackText, int fontSize, int textColor);
    void setCustomText(String text, int fontSize, int textColor);
    void setCustomBackgroundTintList(int light, int dark);
    void setCustomColor(int color);
}
