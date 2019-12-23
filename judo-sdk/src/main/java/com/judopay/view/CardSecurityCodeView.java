package com.judopay.view;

import android.content.Context;
import android.util.AttributeSet;

import com.judopay.R;
import com.judopay.model.CardNetwork;

public class CardSecurityCodeView extends FadeImageView {

    public CardSecurityCodeView(final Context context) {
        super(context);
    }

    public CardSecurityCodeView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CardSecurityCodeView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getImageResource(final int type) {
        if (type == CardNetwork.AMEX) {
            return R.drawable.ic_card_cid;
        } else {
            return R.drawable.ic_card_cvv;
        }
    }
}