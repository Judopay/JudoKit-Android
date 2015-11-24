package com.judopay.payment;

import com.judopay.customer.Card;

public interface PaymentFormListener {

    void onSubmit(Card card, final boolean threeDSecureEnabled);

}