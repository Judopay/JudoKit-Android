package com.judopay.validation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.LinkedHashMap;
import java.util.Map;

import rx.functions.Action1;

public class FormRevealManager {

    public FormRevealManager(Map<ValidationViews, Validator> viewValidators) {
        for (final Map.Entry<ValidationViews, Validator> viewValidator : viewValidators.entrySet()) {
            viewValidator.getValue().onValidate()
                    .subscribe(new Action1<Validation>() {
                        @Override
                        public void call(Validation validation) {
                            ValidationViews views = viewValidator.getKey();

                            if (validation.isValid()) {
                                views.getRevealedView().setVisibility(View.VISIBLE);

                                if (views.getFocusedView() != null) {
                                    views.getFocusedView().requestFocus();
                                }
                            } else {
                                views.getRevealedView().setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    public static class Builder {
        private final LinkedHashMap<ValidationViews, Validator> viewValidators;

        public Builder() {
            this.viewValidators = new LinkedHashMap<>();
        }

        public Builder addField(final View revealedView, Validator validator) {
            viewValidators.put(new ValidationViews(revealedView), validator);
            return this;
        }

        public Builder addField(final View revealedView, final View focusView, Validator validator) {
            viewValidators.put(new ValidationViews(revealedView, focusView), validator);
            return this;
        }

        public FormRevealManager build() {
            return new FormRevealManager(viewValidators);
        }
    }

    private static class ValidationViews {

        private final View revealedView;
        private final View focusedView;

        private ValidationViews(View revealedView) {
            this(revealedView, null);
        }

        private ValidationViews(@NonNull View revealedView, @Nullable View focusedView) {
            this.revealedView = revealedView;
            this.focusedView = focusedView;
        }

        @NonNull
        public View getRevealedView() {
            return revealedView;
        }

        @Nullable
        public View getFocusedView() {
            return focusedView;
        }
    }

}