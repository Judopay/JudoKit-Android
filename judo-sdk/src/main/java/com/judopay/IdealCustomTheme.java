package com.judopay;

import android.graphics.Typeface;

public class IdealCustomTheme {

    private static IdealCustomTheme instance = null;
    private int background;
    private int buttonBackground;
    private int buttonDarkBackground;
    private int spinnerBackgroundColor;
    private int progressBarColor;
    private int fontSize;
    private int buttonFontSize;
    private Typeface typeface;
    private int actionBarColor;
    private int textColor;
    private int buttonTextColor;
    private String payButtonText;
    private String successLabelText;
    private String failLabelText;
    private String pendingLabelText;
    private String successButtonText;
    private String failButtonText;
    private String bankHint;
    private String bankLabel;
    private String nameHint;

    public static IdealCustomTheme getInstance() {
        if (instance == null) {
            instance = new IdealCustomTheme();
        }
        return instance;
    }

    public String getPayButtonText() {
        return payButtonText;
    }

    public void setPayButtonText(String payButtonText) {
        this.payButtonText = payButtonText;
    }

    public String getFailLabelText() {
        return failLabelText;
    }

    public void setFailLabelText(String failLabelText) {
        this.failLabelText = failLabelText;
    }

    public String getPendingLabelText() {
        return pendingLabelText;
    }

    public void setPendingLabelText(String pendingLabelText) {
        this.pendingLabelText = pendingLabelText;
    }

    public String getSuccessButtonText() {
        return successButtonText;
    }

    public void setSuccessButtonText(String successButtonText) {
        this.successButtonText = successButtonText;
    }

    public String getFailButtonText() {
        return failButtonText;
    }

    public void setFailButtonText(String failButtonText) {
        this.failButtonText = failButtonText;
    }

    public void setSuccessLabelText(String successLabelText) {
        this.successLabelText = successLabelText;
    }

    public String getSuccessLabelText() {
        return successLabelText;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getButtonBackground() {
        return buttonBackground;
    }

    public void setButtonBackground(int payButtonBackground) {
        this.buttonBackground = payButtonBackground;
    }

    public int getSpinnerBackgroundColor() {
        return spinnerBackgroundColor;
    }

    public void setSpinnerBackgroundColor(int spinnerBackgroundColor) {
        this.spinnerBackgroundColor = spinnerBackgroundColor;
    }

    public int getProgressBarColor() {
        return progressBarColor;
    }

    public void setProgressBarColor(int progressBarColor) {
        this.progressBarColor = progressBarColor;
    }

    public int getActionBarColor() {
        return actionBarColor;
    }

    public void setActionBarColor(int actionBarColor) {
        this.actionBarColor = actionBarColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getButtonTextColor() {
        return buttonTextColor;
    }

    public void setButtonTextColor(int buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    public String getBankHint() {
        return this.bankHint;
    }

    public void setBankHint(String bankHint) {
        this.bankHint = bankHint;
    }

    public String getNameHint() {
        return nameHint;
    }

    public void setNameHint(String nameHint) {
        this.nameHint = nameHint;
    }

    public int getButtonFontSize() {
        return buttonFontSize;
    }

    public void setButtonFontSize(int buttonFontSize) {
        this.buttonFontSize = buttonFontSize;
    }

    public int getButtonDarkBackground() {
        return buttonDarkBackground;
    }

    public void setButtonDarkBackground(int buttonDarkBackground) {
        this.buttonDarkBackground = buttonDarkBackground;
    }

    public String getBankLabel() {
        return bankLabel;
    }

    public void setBankLabel(String bankLabel) {
        this.bankLabel = bankLabel;
    }
}
