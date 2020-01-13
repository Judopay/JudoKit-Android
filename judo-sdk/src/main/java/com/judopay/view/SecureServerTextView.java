package com.judopay.view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.judopay.R;

public class SecureServerTextView extends AppCompatTextView {

    public SecureServerTextView(final Context context) {
        super(context);
        initialize();
    }

    public SecureServerTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SecureServerTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        final SpannableStringBuilder spannable = new SpannableStringBuilder(getResources().getString(R.string.secure_server));
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.append(" ").append(getResources().getString(R.string.secure_server_transmission));
        setText(spannable);
    }

}