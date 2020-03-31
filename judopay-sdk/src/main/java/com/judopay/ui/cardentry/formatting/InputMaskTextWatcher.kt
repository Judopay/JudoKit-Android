package com.judopay.ui.cardentry.formatting

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

internal fun Char.isPlaceHolder(): Boolean = this == '#'

internal open class InputMaskTextWatcher(
    private val editText: EditText,
    var mask: String
) : TextWatcher {

    private var isSelfFormatting = false

    override fun afterTextChanged(s: Editable?) {
        if (isSelfFormatting) return

        isSelfFormatting = true
        format(s)
        isSelfFormatting = false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    fun format(text: Editable?) {
        if (text.isNullOrEmpty()) return
        text.apply {
            // reset input filters
            val editableFilters = filters
            filters = emptyArray()

            val formatted = StringBuilder()
            val list = toMutableList()

            // apply mask
            mask.forEach { m ->
                if (list.isNullOrEmpty()) return@forEach
                var c = list[0]
                if (m.isPlaceHolder()) {
                    if (!c.isLetterOrDigit()) {
                        // find next letter or digit
                        val iterator = list.iterator()
                        while (iterator.hasNext()) {
                            c = iterator.next()
                            if (c.isLetterOrDigit()) break
                            iterator.remove()
                        }
                    }
                    if (list.isNullOrEmpty()) return@forEach
                    formatted.append(c)
                    list.removeAt(0)
                } else {
                    formatted.append(m)
                    if (m == c) {
                        list.removeAt(0)
                    }
                }
            }
            val previousLength = length
            val currentLength = formatted.length
            replace(0, previousLength, formatted, 0, currentLength)

            // set correct cursor position when editing
            if (currentLength < previousLength) {
                val currentSelection = findCursorPosition(text, editText.selectionStart)
                editText.setSelection(currentSelection)
            }

            // restore input filters
            filters = editableFilters
        }
    }

    private fun findCursorPosition(text: Editable?, start: Int): Int {
        if (text.isNullOrEmpty()) return start
        val textLength = text.length
        val maskLength = mask.length
        var position = start
        for (i in start until maskLength) {
            if (mask[i].isPlaceHolder()) {
                break
            }
            position++
        }
        position++
        return if (position < textLength) position else textLength
    }
}

internal fun String.unformatted(formatMask: String): String {
    if (formatMask.isEmpty()) return this

    return StringBuilder().apply {
        formatMask.forEachIndexed { index, char ->
            if (index >= length) return@forEachIndexed
            if (char.isPlaceHolder()) {
                append(this[index])
            }
        }
    }.toString()
}
