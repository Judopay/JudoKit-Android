package com.judopay.validation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class ValidationManager {
    private final Map<Validator, Boolean> validationResults;
    private final Map<Validator, Disposable> disposables;
    private final OnChangeListener onValidationChangeListener;

    public ValidationManager(List<Validator> validators, OnChangeListener onValidationChangeListener) {
        this.onValidationChangeListener = onValidationChangeListener;
        this.validationResults = new LinkedHashMap<>();
        this.disposables = new LinkedHashMap<>();

        for (final Validator validator : validators) {
            addValidator(validator);
        }
    }

    public void addValidator(final Validator validator) {
        addValidator(validator, validator.onValidate());
    }

    public void addValidator(final Validator validator, Observable<Validation> observable) {
        if (!validationResults.containsKey(validator)) {
            validationResults.put(validator, false);

            Disposable subscription = observable.subscribe(validation -> {
                validationResults.remove(validator);
                validationResults.put(validator, validation.isValid());
                notifyListener();
            });

            disposables.put(validator, subscription);
        }
    }

    Map<Validator, Boolean> getValidationResults() {
        return validationResults;
    }

    public void removeValidator(final Validator validator) {
        if (disposables.containsKey(validator)) {
            Disposable subscription = disposables.get(validator);
            subscription.dispose();

            disposables.remove(validator);
            validationResults.remove(validator);
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

    public interface OnChangeListener {
        void onValidate(boolean valid);
    }
}
