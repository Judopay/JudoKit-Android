package com.judopay.view;

import android.text.Editable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CardNumberFormattingTextWatcherTest {

    @Mock
    Editable editable;

    @Test
    public void shouldInsertFirstSpaceWhenVisa() {
        when(editable.toString()).thenReturn("4976");
        when(editable.length()).thenReturn(4);

        CardNumberFormattingTextWatcher textWatcher = new CardNumberFormattingTextWatcher();

        textWatcher.afterTextChanged(editable);

        verify(editable).append(" ");
    }

    @Test
    public void shouldInsertFirstSpaceWhenAmex() {
        when(editable.toString()).thenReturn("3400");
        when(editable.length()).thenReturn(4);

        CardNumberFormattingTextWatcher textWatcher = new CardNumberFormattingTextWatcher();

        textWatcher.afterTextChanged(editable);

        verify(editable).append(" ");
    }

}