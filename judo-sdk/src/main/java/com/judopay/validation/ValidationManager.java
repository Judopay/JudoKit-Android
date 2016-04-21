package com.judopay.validation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;

public class ValidationManager {

    private final Map<Validator, Boolean> validationResults;
    private final Map<Validator, Subscription> subscriptions;
    private final OnChangeListener onValidationChangeListener;

    public interface OnChangeListener {
        void onValidate(boolean valid);
    }

    public ValidationManager(List<Validator> validators, OnChangeListener onValidationChangeListener) {
        this.onValidationChangeListener = onValidationChangeListener;
        this.validationResults = new LinkedHashMap<>();
        this.subscriptions = new LinkedHashMap<>();

        for (final Validator validator : validators) {
            addValidator(validator);
        }
    }

    public void addValidator(final Validator validator) {
        if(!validationResults.containsKey(validator)) {
            validationResults.put(validator, false);

            Subscription subscription = validator.onValidate().subscribe(new Action1<Validation>() {
                @Override
                public void call(Validation validation) {
                    if(validationResults.containsKey(validator)) {
                        validationResults.remove(validator);
                    }
                    validationResults.put(validator, validation.isValid());
                    notifyListener();
                }
            });

            subscriptions.put(validator, subscription);
        }
    }

    public void removeValidator(final Validator validator) {
        if(subscriptions.containsKey(validator)) {
            Subscription subscription = subscriptions.get(validator);
            subscription.unsubscribe();

            subscriptions.remove(validator);
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

}