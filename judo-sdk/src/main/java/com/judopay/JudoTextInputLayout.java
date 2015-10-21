package com.judopay;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;

public class JudoTextInputLayout extends android.support.design.widget.TextInputLayout {

    public JudoTextInputLayout(Context context) {
        super(context);
    }

    public JudoTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JudoTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        try {
            // silently ignores a NullPointerException caused by Android Design TextInputLayout
            super.dispatchRestoreInstanceState(container);
        } catch (NullPointerException ignore) { }
    }

}
