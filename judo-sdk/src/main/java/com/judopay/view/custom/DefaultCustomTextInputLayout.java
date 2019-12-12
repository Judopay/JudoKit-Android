package com.judopay.view.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Field;

public class DefaultCustomTextInputLayout extends TextInputLayout implements CustomTextInputLayout {

    public DefaultCustomTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCustomHint(String text, int fontSize, int color, Typeface typeface) {
        if (text != null) {
            super.setHint(text);
        }
        if (fontSize != 0) {
            getEditText().setTextSize(fontSize);
        }
        if (typeface != null) {
            setTypeface(typeface);
        }
        if (color != 0) {
            Drawable background = getEditText().getBackground();
            DrawableCompat.setTint(background, color);
            getEditText().setBackground(background);
            try {
                Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("defaultHintTextColor");
                fDefaultTextColor.setAccessible(true);
                fDefaultTextColor.set(this, new ColorStateList(new int[][]{{0}}, new int[]{color}));

                Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("focusedTextColor");
                fFocusedTextColor.setAccessible(true);
                fFocusedTextColor.set(this, new ColorStateList(new int[][]{{0}}, new int[]{color}));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
