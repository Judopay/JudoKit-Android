package com.judopay.validation;

import android.util.Pair;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;

import static java.lang.Boolean.FALSE;

public class ValidationAutoAdvanceManager {
    private static CompositeDisposable disposables = new CompositeDisposable();

    public static void bind(final ValidationManager validationManager, final List<Pair<Validator, View>> validatorViews) {
        final List<Validator> validators = new LinkedList<>();
        final List<View> views = new LinkedList<>();

        disposables.clear();

        for (final Pair<Validator, View> validatorViewPair : validatorViews) {
            validators.add(validatorViewPair.first);
            views.add(validatorViewPair.second);

            disposables.add(validatorViewPair.first.onValidate()
                    .subscribe(validation -> {
                        if (validation.isValid() && validatorViewPair.second.hasFocus()) {
                            int index = validators.indexOf(validatorViewPair.first);
                            Map<Validator, Boolean> validationResults = validationManager.getValidationResults();

                            // Iterate through the remaining validators to find one to focus on
                            for (int i = index; i < validators.size(); i++) {
                                Validator validator = validators.get(i);
                                Boolean valid = validationResults.get(validator);

                                if (valid == FALSE) {
                                    View view = views.get(i);
                                    if (view.isShown() && view.isFocusable() && !view.isFocused()) {
                                        view.requestFocus();
                                        break;
                                    }
                                }
                            }
                        }
                    }));
        }
    }
}
