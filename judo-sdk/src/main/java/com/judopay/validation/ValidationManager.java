package com.judopay.validation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

public class ValidationManager {

    private final Map<Validator, Boolean> validationResults;
    private final OnChangeListener onValidationChangeListener;

    public interface OnChangeListener {
        void onValidate(boolean valid);
    }

    public ValidationManager(List<Validator> validators, OnChangeListener onValidationChangeListener) {
        this.onValidationChangeListener = onValidationChangeListener;
        this.validationResults = new LinkedHashMap<>();

        for (final Validator validator : validators) {
            validationResults.put(validator, false);
            validator.onValidate().subscribe(new Action1<Validation>() {
                @Override
                public void call(Validation validation) {
                    validationResults.put(validator, validation.isValid());
                    notifyListener();
                }
            });
        }
    }

    private void notifyListener() {
        boolean valid = true;

        for (Boolean resultValid : validationResults.values()) {
            if (!resultValid) {
                valid = false;
                break;
            }
        }

        if (onValidationChangeListener != null) {
            onValidationChangeListener.onValidate(valid);
        }
    }

}