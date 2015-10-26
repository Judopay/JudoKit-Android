package com.judopay.payment.form.cvv;

import android.widget.EditText;

import com.judopay.HintFocusListener;
import com.judopay.R;
import com.judopay.customer.CardType;

public class CvvHintFocusListener extends HintFocusListener {

    public CvvHintFocusListener(EditText editText, int hintResourceId) {
        super(editText, hintResourceId);
    }

    public void setCardType(int cardType) {
        if(CardType.AMEX == cardType) {
            setHintResourceId(R.string.amex_cvv_hint);
        } else {
            setHintResourceId(R.string.cvv_hint);
        }
    }

}