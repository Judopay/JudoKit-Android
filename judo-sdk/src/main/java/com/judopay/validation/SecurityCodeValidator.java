package com.judopay.validation;

import android.widget.EditText;

import com.judopay.R;
import com.judopay.model.CardType;
import com.judopay.view.SimpleTextWatcher;

import rx.Observable;
import rx.Subscriber;

public class SecurityCodeValidator implements Validator {

    private int cardType;
    private final EditText editText;

    public SecurityCodeValidator(EditText editText) {
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
                            Validation validation = new Validation(isValid(text), R.string.check_cvv, true);
                            subscriber.onNext(validation);
                        }
                    }
                });
            }
        });
    }

    private boolean isValid(CharSequence text) {
        if (cardType == CardType.AMEX) {
            return text.length() == 4;
        }
        return text.length() == 3;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }
}
