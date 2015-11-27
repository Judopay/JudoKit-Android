package com.judopay;

import android.widget.EditText;

import com.judopay.payment.form.HintFocusListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HintFocusListenerTest {

    @Test @SuppressWarnings("ResourceType")
    public void shouldSetHintWhenFocused() {
        EditText editText = mock(EditText.class);
        int hintResourceId = 123;

        HintFocusListener hintFocusListener = new HintFocusListener(editText, hintResourceId);
        hintFocusListener.onFocusChange(null, true);

        verify(editText).setHint(hintResourceId);
    }

    @Test @SuppressWarnings("ResourceType")
    public void shouldSetEmptyHintWhenBlurred() {
        EditText editText = mock(EditText.class);

        HintFocusListener hintFocusListener = new HintFocusListener(editText, 123);
        hintFocusListener.onFocusChange(null, false);

        verify(editText).setHint(eq(""));
    }

}