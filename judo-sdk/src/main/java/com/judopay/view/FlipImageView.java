package com.judopay.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

public abstract class FlipImageView extends FrameLayout {

    private ImageView frontImageView;
    private ImageView backImageView;

    private int imageResource = 0;

    public FlipImageView(Context context) {
        super(context);
        setCardType(0, false);
    }

    public FlipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCardType(0, false);
    }

    public FlipImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCardType(0, false);
    }

    @DrawableRes
    protected abstract int getImageResource(int type);

    public void setCardType(int cardType, boolean animate) {
        if (this.frontImageView == null) {
            this.frontImageView = new ImageView(getContext());

            this.frontImageView.setImageResource(getImageResource(cardType));
            addView(this.frontImageView);
        }

        if (this.backImageView == null) {
            this.backImageView = new ImageView(getContext());
            this.backImageView.setVisibility(GONE);
            addView(this.backImageView);
        }

        int cardResourceId = getImageResource(cardType);

        if (this.imageResource != cardResourceId) {
            if (animate) {
                flipImages(cardResourceId);
            } else {
                showImage(cardResourceId);
            }
        }
    }

    private void showImage(int cardResourceId) {
        this.imageResource = cardResourceId;
        frontImageView.setImageResource(cardResourceId);
    }

    private void flipImages(int cardResourceId) {
        this.imageResource = cardResourceId;
        backImageView.setImageResource(cardResourceId);

        FlipAnimation flipAnimation = new FlipAnimation(frontImageView, backImageView, 200);
        startAnimation(flipAnimation);

        ImageView temp = frontImageView;
        this.frontImageView = backImageView;
        this.backImageView = temp;
    }

}
