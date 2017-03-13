package com.judopay.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import com.judopay.R;

public class SecureServerTextView extends AppCompatTextView {

    public SecureServerTextView(Context context) {
        super(context);
        initialize();
    }

    public SecureServerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SecureServerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        SpannableString spannable = new SpannableString(getResources().getString(R.string.secure_server_transmission));

        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setText(spannable);
    }

}