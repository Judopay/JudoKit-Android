package com.judopay.ui.cardentry.components

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class InputMask(private val mEditText: EditText,
                var mMask: String) : TextWatcher {

    private var isUpdating = false
    private var mOldString = ""

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val str = s.toString().replace("[^\\d]".toRegex(), "")
        val mask = StringBuilder()
        if (isUpdating) {
            mOldString = str
            isUpdating = false
            return
        }
        var i = 0
        for (m in mMask.toCharArray()) {
            if (m != '#' && str.length > mOldString.length) {
                mask.append(m)
                continue
            }
            try {
                mask.append(str[i])
            } catch (e: Exception) {
                break
            }
            i++
        }
        isUpdating = true
        mEditText.setText(mask.toString())
        mEditText.setSelection(mask.length)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}
}
