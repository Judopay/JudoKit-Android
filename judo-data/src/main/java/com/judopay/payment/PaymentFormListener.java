package com.judopay.payment;

import com.judopay.model.Card;

public interface PaymentFormListener {

    void onSubmit(Card card);

}