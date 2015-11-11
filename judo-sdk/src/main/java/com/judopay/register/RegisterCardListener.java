package com.judopay.register;

import com.judopay.payment.Receipt;

public interface RegisterCardListener {

    void onSuccess(Receipt receipt);

    void onDeclined(Receipt receipt);

}