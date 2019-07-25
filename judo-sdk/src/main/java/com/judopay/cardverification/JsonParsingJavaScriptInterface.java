package com.judopay.cardverification;

import static com.judopay.arch.TextUtil.isEmpty;

public class JsonParsingJavaScriptInterface {

    private final JsonListener jsonListener;

    public JsonParsingJavaScriptInterface(final JsonListener jsonListener) {
        this.jsonListener = jsonListener;
    }

    @android.webkit.JavascriptInterface
    public void parseJsonFromHtml(final String content) {
        if (!isEmpty(content)) {
            try {
                String json = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
                jsonListener.onJsonReceived(json);
            } catch (StringIndexOutOfBoundsException ignore) {
            }
        }
    }

    interface JsonListener {
        void onJsonReceived(String json);
    }

}