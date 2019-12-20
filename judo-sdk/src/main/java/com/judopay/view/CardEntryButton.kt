package com.judopay.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.StringRes
import com.judopay.R
import kotlinx.android.synthetic.main.view_entry_button.view.entryButton
import kotlinx.android.synthetic.main.view_entry_button.view.progressBar

class CardEntryButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_entry_button, this)
    }

    fun startLoading() {
        progressBar.visibility = View.VISIBLE
        entryButton.apply {
            text = ""
            isEnabled = false
        }
    }

    fun finishLoading(@StringRes label: Int) {
        progressBar.visibility = View.GONE
        entryButton.apply {
            text = resources.getString(label)
            isEnabled = true
        }
    }

    fun getButton(): Button = entryButton
}