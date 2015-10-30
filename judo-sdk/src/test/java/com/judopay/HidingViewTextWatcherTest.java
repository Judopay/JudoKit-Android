package com.judopay;

import android.view.View;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class HidingViewTextWatcherTest {

    @Test
    public void shouldHideViewWhenTextAdded() {
        View view = mock(View.class);
        HidingViewTextWatcher textWatcher = new HidingViewTextWatcher(view);

        textWatcher.onTextChanged("hello!", 0, 0, 0);

        verify(view).setVisibility(View.GONE);
    }

}