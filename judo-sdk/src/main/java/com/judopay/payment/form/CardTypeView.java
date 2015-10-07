package com.judopay.payment.form;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.judopay.R;
import com.judopay.customer.CardType;

public class CardTypeView extends FrameLayout {

    public CardTypeView(Context context) {
        super(context);
        setCardType(0);
    }

    public CardTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCardType(0);
    }

    public CardTypeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCardType(0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardTypeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
                return R.drawable.ic_card_amex;
            case CardType.MASTERCARD:
                return R.drawable.ic_card_mastercard;
            case CardType.MAESTRO:
                return R.drawable.ic_card_maestro;
            case CardType.VISA:
                return R.drawable.ic_card_visa;
            default:
                return R.drawable.ic_card_unknown;
        }
    }

}