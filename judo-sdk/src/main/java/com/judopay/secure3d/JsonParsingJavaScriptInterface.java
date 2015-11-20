package com.judopay.secure3d;

class JsonParsingJavaScriptInterface {

    private final JsonListener jsonListener;

    JsonParsingJavaScriptInterface(JsonListener jsonListener) {
        this.jsonListener = jsonListener;
    }

    @android.webkit.JavascriptInterface
    public void parseJsonFromHtml(final String content) {
        if (content != null && content.length() > 0) {
            try {
                String json = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1);
                jsonListener.onJsonReceived(json);
            } catch (StringIndexOutOfBoundsException ignore) {
            }
        }
    }

    public interface JsonListener {
        void onJsonReceived(String json);
    }

}
