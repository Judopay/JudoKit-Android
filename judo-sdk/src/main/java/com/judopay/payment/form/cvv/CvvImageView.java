package com.judopay.payment.form.cvv;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.judopay.R;
import com.judopay.customer.CardType;

public class CvvImageView extends FrameLayout {

    public CvvImageView(Context context) {
        super(context);
    }

    public CvvImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CvvImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CvvImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
                return R.drawable.ic_card_cidv;
            default:
                return R.drawable.ic_card_cvv;
        }
    }

}
