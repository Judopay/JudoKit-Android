package com.judopay.payment.form;

import android.widget.EditText;

import com.judopay.view.HintFocusListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HintFocusListenerTest {

    @Test
    public void shouldSetHintWhenFocused() {
        EditText editText = mock(EditText.class);

        String hint = "hint";
        HintFocusListener hintFocusListener =  new HintFocusListener(editText, hint);
        hintFocusListener.onFocusChange(null, true);

        verify(editText).setHint(eq(hint));
    }

    @Test
    public void shouldSetEmptyHintWhenBlurred() {
        EditText editText = mock(EditText.class);

        HintFocusListener hintFocusListener = new HintFocusListener(editText, "hint");
        hintFocusListener.onFocusChange(null, false);

        verify(editText).setHint(eq(""));
    }

}