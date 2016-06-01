package com.judopay.validation;

import android.widget.EditText;

import com.judopay.R;
import com.judopay.model.CardDate;
import com.judopay.view.SimpleTextWatcher;

import rx.Observable;
import rx.Subscriber;

public class ExpiryDateValidator implements Validator {

    private final EditText editText;

    public ExpiryDateValidator(EditText editText) {
        this.editText = editText;
    }

    @Override
    public Observable<Validation> onValidate() {
        return Observable.create(new Observable.OnSubscribe<Validation>() {
            @Override
            public void call(final Subscriber<? super Validation> subscriber) {
                editText.addTextChangedListener(new SimpleTextWatcher() {
                    @Override
                    protected void onTextChanged(CharSequence text) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(getValidation(text.toString()));
                        }
                    }
                });
            }
        });
    }

    private Validation getValidation(String text) {
        boolean valid = isExpiryDateValid(text);
        boolean expiryLengthValid = text.length() == 5;

        return new Validation(valid, R.string.check_expiry_date, !valid && expiryLengthValid);
    }

    private boolean isExpiryDateValid(String expiryDate) {
        CardDate cardDate = new CardDate(expiryDate);
        return cardDate.isAfterToday() && cardDate.isInsideAllowedDateRange();
    }

}