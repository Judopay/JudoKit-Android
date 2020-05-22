package com.judokit.android.ui.cardverification.components

class JsonParsingJavaScriptInterface(private val onJsonReceived: (String) -> Unit) {

    @android.webkit.JavascriptInterface
    fun parseJsonFromHtml(content: String?) {
        if (!content.isNullOrEmpty()) {
            try {
                val json = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1)
                onJsonReceived.invoke(json)
            } catch (exception: StringIndexOutOfBoundsException) {
                exception.printStackTrace()
            }
        }
    }
}
