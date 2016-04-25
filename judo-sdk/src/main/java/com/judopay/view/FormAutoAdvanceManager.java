package com.judopay.view;

import android.util.Pair;
import android.view.View;

import com.judopay.validation.Validation;
import com.judopay.validation.ValidationManager;
import com.judopay.validation.Validator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

public class FormAutoAdvanceManager {

    private final List<Validator> validators;
    private final List<View> views;

    public FormAutoAdvanceManager(final ValidationManager validationManager, List<Pair<Validator, View>> validatorViews) {
        this.validators = new LinkedList<>();
        this.views = new LinkedList<>();

        for (final Pair<Validator, View> validatorViewPair : validatorViews) {
            this.validators.add(validatorViewPair.first);
            this.views.add(validatorViewPair.second);

            validatorViewPair.first.onValidate()
                    .subscribe(new Action1<Validation>() {
                        @Override
                        public void call(Validation validation) {
                            if (validation.isValid() && validatorViewPair.second.hasFocus()) {
                                int index = validators.indexOf(validatorViewPair.first);
                                Map<Validator, Boolean> validationResults = validationManager.getValidationResults();

                                // iterate through remaining validators to find one to focus on.
                                for (int i = index; i < validators.size(); i++) {
                                    Validator validator = validators.get(i);
                                    Boolean valid = validationResults.get(validator);

                                    if (valid != null && !valid) {
                                        View view = views.get(i);
                                        view.requestFocus();
                                        break;
                                    }
                                }
                            }
                        }
                    });
        }
    }


}
