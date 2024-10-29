package com.judopay.judokit.android.ui.paymentmethods.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.judopay.judokit.android.R
import com.judopay.judokit.android.databinding.PollingStatusViewBinding
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewState.DELAY
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewState.FAIL
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewState.PROCESSING
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewState.RETRY
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewState.SUCCESS

enum class PollingStatusViewState {
    PROCESSING,
    DELAY,
    RETRY,
    FAIL,
    SUCCESS,
    ;

    val action: PollingStatusViewAction
        get() =
            when (this) {
                DELAY, RETRY -> PollingStatusViewAction.RETRY
                FAIL, SUCCESS, PROCESSING -> PollingStatusViewAction.CLOSE
            }
}

enum class PollingStatusViewAction {
    CLOSE,
    RETRY,
}

typealias PollingStatusViewButtonClickListener = (PollingStatusViewAction) -> Unit

class PollingStatusView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyle) {
        val binding = PollingStatusViewBinding.inflate(LayoutInflater.from(context), this, true)

        internal var state: PollingStatusViewState? = null
            set(value) {
                field = value
                updateState()
            }

        internal var onButtonClickListener: PollingStatusViewButtonClickListener? = null

        private val pollingTextResId: Int
            get() =
                when (state) {
                    PROCESSING -> R.string.jp_processing
                    DELAY -> R.string.jp_there_is_a_delay
                    RETRY, FAIL -> R.string.jp_transaction_unsuccessful
                    SUCCESS -> R.string.jp_transaction_successful
                    else -> R.string.jp_empty
                }

        private val pollingButtonTextResId: Int
            get() =
                when (state) {
                    DELAY, RETRY -> R.string.jp_retry
                    FAIL, SUCCESS -> R.string.jp_close
                    else -> R.string.jp_empty
                }

        private val progressBarVisibility: Int
            get() =
                when (state) {
                    PROCESSING, DELAY -> View.VISIBLE
                    else -> View.GONE
                }

        private val pollingButtonVisibility: Int
            get() =
                when (state) {
                    PROCESSING -> View.GONE
                    else -> View.VISIBLE
                }

        init {
            binding.pollingButton.setOnClickListener { handleButtonClick() }
        }

        private fun updateState() {
            if (state == null) {
                return
            }

            visibility = View.VISIBLE

            binding.pollingProgressBar.visibility = progressBarVisibility
            binding.pollingTextView.text = resources.getString(pollingTextResId)

            binding.pollingButton.apply {
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
