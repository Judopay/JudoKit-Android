package com.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.judokit.android.R
import com.judokit.android.inflate
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewState.DELAY
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewState.FAIL
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewState.PROCESSING
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewState.RETRY
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewState.SUCCESS
import kotlinx.android.synthetic.main.polling_status_view.view.*

enum class PollingStatusViewState {
    PROCESSING,
    DELAY,
    RETRY,
    FAIL,
    SUCCESS;

    val action: PollingStatusViewAction
        get() = when (this) {
            DELAY, RETRY -> PollingStatusViewAction.RETRY
            FAIL, SUCCESS, PROCESSING -> PollingStatusViewAction.CLOSE
        }
}

enum class PollingStatusViewAction {
    CLOSE,
    RETRY
}

typealias PollingStatusViewButtonClickListener = (PollingStatusViewAction) -> Unit

class PollingStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    internal var state: PollingStatusViewState? = null
        set(value) {
            field = value
            updateState()
        }

    internal var onButtonClickListener: PollingStatusViewButtonClickListener? = null

    private val pollingTextResId: Int
        get() = when (state) {
            PROCESSING -> R.string.processing
            DELAY -> R.string.there_is_a_delay
            RETRY, FAIL -> R.string.transaction_unsuccessful
            SUCCESS -> R.string.transaction_successful
            else -> R.string.empty
        }

    private val pollingButtonTextResId: Int
        get() = when (state) {
            DELAY, RETRY -> R.string.retry
            FAIL, SUCCESS -> R.string.close
            else -> R.string.empty
        }

    private val progressBarVisibility: Int
        get() = when (state) {
            PROCESSING, DELAY -> View.VISIBLE
            else -> View.GONE
        }

    private val pollingButtonVisibility: Int
        get() = when (state) {
            PROCESSING -> View.GONE
            else -> View.VISIBLE
        }

    init {
        inflate(R.layout.polling_status_view, true)

        pollingButton.setOnClickListener { handleButtonClick() }

        backButton.setOnClickListener {
            visibility = View.GONE
            handleButtonClick()
        }
    }

    private fun updateState() {
        if (state == null) {
            return
        }

        visibility = View.VISIBLE

        pollingProgressBar.visibility = progressBarVisibility
        pollingTextView.text = resources.getString(pollingTextResId)

        pollingButton.apply {
            visibility = pollingButtonVisibility
            text = resources.getString(pollingButtonTextResId)
        }
    }

    private fun handleButtonClick() {
        val action = state?.action
        action?.let {
            onButtonClickListener?.invoke(it)
        }
    }
}
