package com.judokit.android.ui.cardverification.components

import com.judokit.android.ui.cardverification.component.JsonParsingJavaScriptInterface
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class JsonParsingJavaScriptInterfaceTest {

    private val listener: (String) -> Unit = mockk(relaxed = true)
    private val sut = JsonParsingJavaScriptInterface(listener)

    @DisplayName("Given parsed json is valid, then receive json")
    @Test
    fun shouldReceiveJsonWhenValidJsonParsed() {
        sut.parseJsonFromHtml("<body> {key: \"value\"} </body>")

        verify { listener.invoke("{key: \"value\"}") }
    }

    @DisplayName("Given parsed json is empty, then should handle empty content")
    @Test
    fun shouldHandleEmptyContent() {
        sut.parseJsonFromHtml("")

        verify(inverse = true) { listener.invoke(any()) }
    }

    @DisplayName("Given there is no json, then should handle content")
    @Test
    fun shouldHandleContentWithoutJson() {
        sut.parseJsonFromHtml("<body></body>")

        verify(inverse = true) { listener.invoke(any()) }
    }
}
