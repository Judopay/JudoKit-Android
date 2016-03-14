package com.judopay.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.judopay.R;
import com.judopay.model.CardType;

public class CardSecurityCodeView extends FrameLayout {

    public CardSecurityCodeView(Context context) {
        super(context);
        setCardType(0);
    }

    public CardSecurityCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCardType(0);
    }

    public CardSecurityCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCardType(0);
    }

    public void setCardType(int cardType) {
        removeAllViews();

        int cardResourceId = getCardResourceId(cardType);

        ImageView cardImageView = new ImageView(getContext());
        cardImageView.setImageResource(cardResourceId);

        addView(cardImageView);
    }

    @DrawableRes
    private int getCardResourceId(int cardType) {
        switch (cardType) {
            case CardType.AMEX:
                return R.drawable.ic_card_cid;
            default:
                return R.drawable.ic_card_security_code;
        }
    }

}