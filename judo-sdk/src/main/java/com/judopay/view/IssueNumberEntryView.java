package com.judopay.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.judopay.R;

public class IssueNumberEntryView extends LinearLayout {

    private EditText issueNumberEditText;

    public IssueNumberEntryView(Context context) {
        super(context);
        initialize();
    }

    public IssueNumberEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public IssueNumberEntryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.view_issue_number_entry, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        issueNumberEditText = (EditText) findViewById(R.id.issue_number_edit_text);
        View issueNumberHelperText = findViewById(R.id.issue_number_helper_text);

        issueNumberEditText.setOnFocusChangeListener(new CompositeOnFocusChangeListener(
                new EmptyTextHintOnFocusChangeListener(issueNumberHelperText),
                new HintFocusListener(issueNumberEditText, R.string.issue_number_hint)
        ));

        issueNumberEditText.addTextChangedListener(new HidingViewTextWatcher(issueNumberHelperText));
    }

    public void addTextChangedListener(SimpleTextWatcher watcher) {
        issueNumberEditText.addTextChangedListener(watcher);
    }

    public String getText() {
        return issueNumberEditText.getText().toString().trim();
    }

}