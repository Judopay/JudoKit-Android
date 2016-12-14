package com.judopay.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class FlipImageView extends FrameLayout {

    private AppCompatImageView frontImageView;
    private AppCompatImageView backImageView;

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

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putFloat("alpha", getAlpha());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("superState");

            setAlpha(bundle.getFloat("alpha"));
        }
        super.onRestoreInstanceState(state);
    }

    public void setCardType(int cardType, boolean animate) {
        if (this.frontImageView == null) {
            this.frontImageView = new AppCompatImageView(getContext());
            this.frontImageView.setImageResource(getImageResource(cardType));
            addView(this.frontImageView);
        }

        if (this.backImageView == null) {
            this.backImageView = new AppCompatImageView(getContext());
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

        FlipAnimation flipAnimation = new FlipAnimation(frontImageView, backImageView);
        startAnimation(flipAnimation);

        AppCompatImageView temp = frontImageView;
        this.frontImageView = backImageView;
        this.backImageView = temp;
    }

}