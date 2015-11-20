package com.judopay.payment;

import com.judopay.Consumer;
import com.judopay.customer.Card;

public interface PaymentFormListener {

    void onSubmit(Card card, Consumer consumer, final boolean threeDSecureEnabled);

}