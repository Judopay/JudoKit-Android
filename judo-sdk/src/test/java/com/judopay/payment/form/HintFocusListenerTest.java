package com.judopay.payment.form;

import android.widget.EditText;

import com.judopay.view.HintFocusListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HintFocusListenerTest {

    @Test
    public void shouldSetHintWhenFocused() {
        EditText editText = mock(EditText.class);

        String hint = "hint";
        HintFocusListener hintFocusListener = new HintFocusListener(editText, hint);
        hintFocusListener.onFocusChange(null, true);

        verify(editText).setHint(eq(hint));
    }
}
