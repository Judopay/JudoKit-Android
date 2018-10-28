package com.judopay.validation;

import io.reactivex.Observable;

public interface Validator {

    Observable<Validation> onValidate();
}
