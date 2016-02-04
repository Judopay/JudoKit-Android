package com.judopay.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.judopay.R;
import com.judopay.model.CardType;

/**
 * A view that displays a card type image (Visa, Amex, etc.) to provide the user with feedback
 * that their card type has been recognised.
 */
public class CardTypeImageView extends FrameLayout {

    public CardTypeImageView(Context context) {
        super(context);
        setCardType(0);
    }

    public CardTypeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCardType(0);
    }

    public CardTypeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCardType(0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardTypeImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
            case CardType.VISA_ELECTRON:
            case CardType.VISA_DEBIT:
                return R.drawable.ic_card_visa;
            default:
                return R.drawable.ic_card_unknown;
        }
    }

}
