package com.judopay.payment.form;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.judopay.customer.CardType;

public class CardNumberEditText extends EditText {

    private int lastCardType;

    public interface CardListener {
        void onCardTypeChanged(int cardType);
    }

    private CardListener cardListener;

    public CardNumberEditText(Context context) {
        super(context);
        initialise();
    }

    public CardNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    public CardNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardNumberEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialise();
    }

    public void setCardListener(CardListener cardListener) {
        this.cardListener = cardListener;
    }

    private void initialise() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int cardType = CardType.matchCardNumber(s.toString());
                if (cardListener != null) {
                    if (cardType != lastCardType) {
                        cardListener.onCardTypeChanged(cardType);
                    }
                }
                lastCardType = cardType;
            }
        });
    }

    public boolean isValid() {
        return CardType.matchCardNumber(getText().toString()) != CardType.UNKNOWN;
    }

    public boolean isMaestroCardType() {
        return lastCardType == CardType.MAESTRO;
    }

}