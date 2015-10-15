package com.judopay.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HintedTextInputLayout extends LinearLayout {

    private TextView errorView;
    private TextView labelView;
    private EditText editText;

    public HintedTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        this.errorView = new TextView(context);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            this.editText = (EditText) child;
            this.labelView.setLayoutParams(params);
        } else if (child instanceof TextView) {
            if (index == -1) {
                this.labelView = (TextView) child;
            } else {
                this.errorView = (TextView) child;
//                child.setVisibility(GONE);
            }
        }
        super.addView(child, index, params);
    }

    public HintedTextInputLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HintedTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, (AttributeSet) null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HintedTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, (AttributeSet) null);
    }

    public void setError(@Nullable CharSequence error) {
        if (error != null && error.length() > 0) {
            errorView.setVisibility(VISIBLE);
            errorView.setText(error);
        } else {
            errorView.setVisibility(GONE);
            errorView.setText("");
        }
        errorView.invalidate();
    }

}
