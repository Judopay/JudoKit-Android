package com.judopay.validation;

import android.widget.EditText;

import com.judopay.R;
import com.judopay.model.CardDate;
import com.judopay.view.SimpleTextWatcher;

import io.reactivex.Observable;

public class ExpiryDateValidator implements Validator {
    private final EditText editText;

    public ExpiryDateValidator(final EditText editText) {
        this.editText = editText;
    }

    @Override
    public Observable<Validation> onValidate() {
        return Observable.create(emitter -> {
            emitter.onNext(getValidation(editText.getText().toString()));
            editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onTextChanged(final CharSequence text) {
                emitter.onNext(getValidation(text.toString()));
            }
            });
        });
    }

    private Validation getValidation(final String text) {
        final boolean valid = isExpiryDateValid(text);
        final boolean expiryLengthValid = text.length() == 5;

        return new Validation(valid, R.string.check_expiry_date, !valid && expiryLengthValid);
    }

    private boolean isExpiryDateValid(final String expiryDate) {
        final CardDate cardDate = new CardDate(expiryDate);
        return cardDate.isAfterToday() && cardDate.isInsideAllowedDateRange();
    }
}
