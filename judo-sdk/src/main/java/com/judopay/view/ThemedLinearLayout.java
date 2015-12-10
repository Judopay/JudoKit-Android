package com.judopay.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.judopay.R;

public class ThemedLinearLayout extends LinearLayout {

    public ThemedLinearLayout(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ThemedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.loadingOverlayStyle);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ThemedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ThemedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
