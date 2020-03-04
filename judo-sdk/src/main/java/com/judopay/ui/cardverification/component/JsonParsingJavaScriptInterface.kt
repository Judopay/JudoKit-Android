package com.judopay.ui.cardverification.component

class JsonParsingJavaScriptInterface(private val onJsonReceived: (String) -> Unit) {

    fun parseJsonFromHtml(content: String?) {
        if (!content.isNullOrEmpty()) {
            try {
                val json = content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1)
                onJsonReceived.invoke(json)
            } catch (ignore: StringIndexOutOfBoundsException) {
            }
        }
    }
}