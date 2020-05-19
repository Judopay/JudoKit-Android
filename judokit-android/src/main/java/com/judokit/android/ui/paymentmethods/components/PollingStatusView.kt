package com.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.judokit.android.R
import com.judokit.android.inflate
import kotlinx.android.synthetic.main.polling_status_view.view.*

class PollingStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.polling_status_view, true)
    }

    fun processing(backButtonListener: () -> Unit) {

        backButton.setOnClickListener {
            visibility = View.GONE
            backButtonListener.invoke()
        }
        visibility = View.VISIBLE
        pollingTextView.text = resources.getString(R.string.processing)
        pollingProgressBar.visibility = View.VISIBLE
        pollingButton.visibility = View.GONE
    }

    fun delay(buttonClickListener: () -> Unit) {
        pollingTextView.text = resources.getString(R.string.there_is_a_delay)
        pollingProgressBar.visibility = View.VISIBLE
        pollingButton.apply {
            visibility = View.VISIBLE
            text = resources.getString(R.string.retry)
            setOnClickListener { buttonClickListener.invoke() }
        }
    }

    fun retry(buttonClickListener: () -> Unit) {
        pollingTextView.text = resources.getString(R.string.transaction_unsuccessful)
        pollingProgressBar.visibility = View.GONE
        pollingButton.apply {
            visibility = View.VISIBLE
            text = resources.getString(R.string.retry)
            setOnClickListener { buttonClickListener.invoke() }
        }
    }

    fun fail(buttonClickListener: () -> Unit) {
        pollingTextView.text = resources.getString(R.string.transaction_unsuccessful)
        pollingProgressBar.visibility = View.GONE
        pollingButton.apply {
            visibility = View.VISIBLE
            text = resources.getString(R.string.close)
            setOnClickListener { buttonClickListener.invoke() }
        }
    }

    fun success(buttonClickListener: () -> Unit) {
        pollingTextView.text = resources.getString(R.string.transaction_successful)
        pollingProgressBar.visibility = View.GONE
        pollingButton.apply {
            visibility = View.VISIBLE
            text = resources.getString(R.string.close)
            setOnClickListener { buttonClickListener.invoke() }
        }
    }
}
