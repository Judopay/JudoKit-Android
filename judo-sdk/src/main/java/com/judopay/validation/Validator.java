package com.judopay.validation;

import rx.Observable;

public interface Validator {

    Observable<Validation> onValidate();

}
