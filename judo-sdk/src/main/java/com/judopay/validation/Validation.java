package com.judopay.validation;

import android.support.annotation.StringRes;

public class Validation {

    private final Integer error;
    private final boolean valid;
    private final boolean showError;

    public Validation(boolean valid, @StringRes Integer error, boolean showError) {
        this.valid = valid;
        this.error = error;
        this.showError = showError;
    }

    public boolean isValid() {
        return valid;
    }

    @StringRes
    public Integer getError() {
        return error;
    }

    public boolean isShowError() {
        return showError;
    }
}

