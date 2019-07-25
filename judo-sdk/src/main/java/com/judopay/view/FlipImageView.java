package com.judopay.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class FlipImageView extends FrameLayout {

    private static final String KEY_SUPER_STATE = "superState";
    private static final String KEY_ALPHA = "alpha";
    private static final String KEY_IMAGE_TYPE = "imageType";

    private AppCompatImageView frontImageView;
    private AppCompatImageView backImageView;

    private int imageType;
    private int imageResId;

    public FlipImageView(final Context context) {
        super(context);
        setImageType(0, false);
    }

    public FlipImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setImageType(0, false);
    }

    public FlipImageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageType(0, false);
    }

    @DrawableRes
    protected abstract int getImageResource(final int type);

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState());
        bundle.putFloat(KEY_ALPHA, getAlpha());
        bundle.putInt(KEY_IMAGE_TYPE, imageType);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            final Parcelable superState = bundle.getParcelable(KEY_SUPER_STATE);
            setAlpha(bundle.getFloat(KEY_ALPHA));
            imageType = bundle.getInt(KEY_IMAGE_TYPE);
            setImageType(imageType, false);
            super.onRestoreInstanceState(superState);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void setImageType(final int imageType, final boolean animate) {
        this.imageType = imageType;

        if (this.frontImageView == null) {
            this.frontImageView = new AppCompatImageView(getContext());
            this.frontImageView.setImageResource(getImageResource(imageType));
            addView(this.frontImageView);
        }

        if (this.backImageView == null) {
            this.backImageView = new AppCompatImageView(getContext());
            this.backImageView.setVisibility(GONE);
            addView(this.backImageView);
        }

        int imageResId = getImageResource(imageType);

        if (this.imageResId != imageResId) {
            if (animate) {
                flipImages(imageResId);
            } else {
                showImage(imageResId);
            }
        }
    }

    private void showImage(final int imageResId) {
        this.imageResId = imageResId;
        frontImageView.setImageResource(imageResId);
    }

    private void flipImages(final int imageResId) {
        this.imageResId = imageResId;
        backImageView.setImageResource(imageResId);

        FlipAnimation flipAnimation = new FlipAnimation(frontImageView, backImageView);
        startAnimation(flipAnimation);

        AppCompatImageView temp = frontImageView;
        this.frontImageView = backImageView;
        this.backImageView = temp;
    }

}