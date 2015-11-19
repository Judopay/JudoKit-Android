package com.judopay.secure3d;

import org.junit.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class JsonParsingJavaScriptInterfaceTest {

    @Test
    public void shouldReceiveJsonWhenValidJsonParsed() {
        JsonParsingJavaScriptInterface.JsonListener listener = mock(JsonParsingJavaScriptInterface.JsonListener.class);

        JsonParsingJavaScriptInterface jsonParsingJsInterface = new JsonParsingJavaScriptInterface(listener);
        jsonParsingJsInterface.parseJsonFromHtml("<body> {key: \"value\"} </body>");

        verify(listener).onJsonReceived(eq("{key: \"value\"}"));
    }

    @Test
    public void shouldHandleEmptyContent() {
        JsonParsingJavaScriptInterface.JsonListener listener = mock(JsonParsingJavaScriptInterface.JsonListener.class);

        JsonParsingJavaScriptInterface jsonParsingJsInterface = new JsonParsingJavaScriptInterface(listener);

        jsonParsingJsInterface.parseJsonFromHtml("");

        verify(listener, never()).onJsonReceived(anyString());
    }

    @Test
    public void shouldHandleContentWithoutJson() {
        JsonParsingJavaScriptInterface.JsonListener listener = mock(JsonParsingJavaScriptInterface.JsonListener.class);

        JsonParsingJavaScriptInterface jsonParsingJsInterface = new JsonParsingJavaScriptInterface(listener);

        jsonParsingJsInterface.parseJsonFromHtml("<body></body>");

        verify(listener, never()).onJsonReceived(anyString());
    }

}