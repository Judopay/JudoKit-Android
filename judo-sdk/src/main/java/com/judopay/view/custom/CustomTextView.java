package com.judopay.view.custom;

public interface CustomTextView {
    void setCustomTextWithFallback(String text, int fallbackText, int fontSize, int textColor);
    void setCustomText(String text, int fontSize, int textColor);
}
