package com.judopay.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.google.android.material.textfield.TextInputLayout;

public class JudoTextInputLayout extends TextInputLayout {

    public JudoTextInputLayout(final Context context) {
        super(context);
    }

    public JudoTextInputLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public JudoTextInputLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchRestoreInstanceState(final SparseArray<Parcelable> container) {
        try {
            // silently ignores a NullPointerException caused by Android Design TextInputLayout
            super.dispatchRestoreInstanceState(container);
        } catch (NullPointerException ignore) { }
    }

}
