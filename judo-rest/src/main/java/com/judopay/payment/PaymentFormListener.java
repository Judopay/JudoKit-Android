package com.judopay.payment;

import com.judopay.customer.Card;

public interface PaymentFormListener {

    void onSubmitCard(Card card);

}