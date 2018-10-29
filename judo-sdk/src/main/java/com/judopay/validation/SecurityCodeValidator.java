package com.judopay.validation;

import android.widget.EditText;

import com.judopay.R;
import com.judopay.model.CardNetwork;
import com.judopay.view.SimpleTextWatcher;

import io.reactivex.Observable;

public class SecurityCodeValidator implements Validator {
    private final EditText editText;
    private int cardType;

    public SecurityCodeValidator(EditText editText) {
        this.editText = editText;
    }

    @Override
    public Observable<Validation> onValidate() {
        return Observable.create(emitter -> editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onTextChanged(CharSequence text) {
                Validation validation = new Validation(isValid(text), R.string.check_cvv, true);
                emitter.onNext(validation);
            }
        }));
    }

    private boolean isValid(CharSequence text) {
        if (cardType == CardNetwork.AMEX) {
            return text.length() == 4;
        }
        return text.length() == 3;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }
}
