package com.judopay.view;

import android.content.Context;
import android.util.AttributeSet;

import com.judopay.R;
import com.judopay.model.CardNetwork;

public class CardSecurityCodeView extends FlipImageView {

    public CardSecurityCodeView(Context context) {
        super(context);
    }

    public CardSecurityCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardSecurityCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getImageResource(int type) {
        switch (type) {
            case CardNetwork.AMEX:
                return R.drawable.ic_card_cid;
            default:
                return R.drawable.ic_card_cvv;
        }
    }
}