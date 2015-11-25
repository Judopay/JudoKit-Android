package com.judopay.payment.form;

import com.judopay.customer.Card;

public interface PaymentFormListener {

    void onSubmit(Card card);

}