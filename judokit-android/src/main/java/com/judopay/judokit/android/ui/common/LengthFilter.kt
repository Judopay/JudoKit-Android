package com.judopay.judokit.android.ui.common

import android.text.InputFilter
import android.text.Spanned

class LengthFilter(
    private val max: Int,
    private val onLengthChanged: (reachedMaxLength: Boolean) -> Unit
) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {

        var keep = max - (dest.length - (dend - dstart))

        return when {
            keep <= 0 -> {
                onLengthChanged(true)
                ""
            }
            keep >= end - start -> {
                onLengthChanged(false)
                null
            } // keep original
            else -> {
                keep += start
                if (Character.isHighSurrogate(source[keep - 1])) {
                    --keep
                    if (keep == start) {
                        onLengthChanged(false)
                        return ""
                    }
                }
                onLengthChanged(true)
                source.subSequence(start, keep)
            }
        }
    }
}
