package com.judopay.view;

import android.content.Context;
import android.util.AttributeSet;

import com.judopay.R;
import com.judopay.model.CardNetwork;

/**
 * A view that displays a card type image (Visa, Amex, etc.) to provide the user with feedback
 * that their card type has been recognised.
 */
public class CardTypeImageView extends FlipImageView {

    public CardTypeImageView(final Context context) {
        super(context);
    }

    public CardTypeImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CardTypeImageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getImageResource(final int type) {
        switch (type) {
            case CardNetwork.AMEX:
                return R.drawable.ic_card_amex;
            case CardNetwork.MASTERCARD:
                return R.drawable.ic_card_mastercard;
            case CardNetwork.MAESTRO:
                return R.drawable.ic_card_maestro;
            case CardNetwork.VISA:
            case CardNetwork.VISA_ELECTRON:
            case CardNetwork.VISA_DEBIT:
                return R.drawable.ic_card_visa;
            default:
                return R.drawable.ic_card_unknown;
        }
    }
}