package com.judopay.payment.form;

import com.judopay.model.Card;

public interface PaymentFormListener {

    void onSubmit(Card card);

}