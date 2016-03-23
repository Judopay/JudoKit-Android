package com.judopay.view;

import android.content.Context;
import android.util.AttributeSet;

import com.judopay.R;
import com.judopay.model.CardType;

/**
 * A view that displays a card type image (Visa, Amex, etc.) to provide the user with feedback
 * that their card type has been recognised.
 */
public class CardTypeImageView extends FlipImageView {

    public CardTypeImageView(Context context) {
        super(context);
    }

    public CardTypeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardTypeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getImageResource(int type) {
        switch (type) {
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
