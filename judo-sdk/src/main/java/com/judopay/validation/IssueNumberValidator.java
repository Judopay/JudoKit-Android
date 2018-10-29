package com.judopay.validation;

import android.widget.EditText;

import com.judopay.view.SimpleTextWatcher;

import io.reactivex.Observable;

public class IssueNumberValidator implements Validator {
    private final EditText editText;

    public IssueNumberValidator(EditText editText) {
        this.editText = editText;
    }

    @Override
    public Observable<Validation> onValidate() {
        return Observable.create(emitter -> editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onTextChanged(CharSequence text) {
                emitter.onNext(getValidation(text.toString()));
            }
        }));
    }

    private Validation getValidation(String text) {
        boolean valid = isIssueNumberValid(text);
        return new Validation(valid, 0, false);
    }

    private boolean isIssueNumberValid(String issueNumber) {
        try {
            int issueNo = Integer.parseInt(issueNumber);
            return issueNo > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
