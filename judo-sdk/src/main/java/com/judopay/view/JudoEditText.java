package com.judopay.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Collection;

public class JudoEditText extends AppCompatEditText implements MultiFocusable, PasteListenable {

    private Collection<TextWatcher> textWatchers;
    private PasteListener pasteListener;
    private MultiOnFocusChangeListener multiOnFocusChangeListener;

    public JudoEditText(final Context context) {
        super(context);
        initialize();
    }

    public JudoEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public JudoEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        textWatchers = new ArrayList<>();
        multiOnFocusChangeListener = new MultiOnFocusChangeListener();
        super.setOnFocusChangeListener(multiOnFocusChangeListener);
    }

    @Override
    public void setPasteListener(final PasteListener pasteListener) {
        this.pasteListener = pasteListener;
    }

    public Collection<TextWatcher> getTextWatchers() {
        return textWatchers;
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener listener) {
        multiOnFocusChangeListener.add(listener);
    }

    public void addTextChangedListeners(final Collection<TextWatcher> watchers) {
        for (TextWatcher watcher : watchers) {
            addTextChangedListener(watcher);
        }
    }

    public void removeTextChangedListeners() {
        for (TextWatcher watcher : textWatchers) {
            super.removeTextChangedListener(watcher);
        }
        textWatchers = new ArrayList<>();
    }

    @Override
    public boolean onTextContextMenuItem(final int id) {
        boolean result = super.onTextContextMenuItem(id);

        if (id == android.R.id.paste && pasteListener != null) {
            pasteListener.onPaste();
        }

        return result;
    }

    @Override
    public void addTextChangedListener(final TextWatcher watcher) {
        super.addTextChangedListener(watcher);
        this.textWatchers.add(watcher);
    }

    @Override
    public void removeTextChangedListener(final TextWatcher watcher) {
        super.removeTextChangedListener(watcher);
        this.textWatchers.remove(watcher);
    }
}
