package com.judopay.judokit.android.ui.common

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.judopay.judokit.android.R
import com.judopay.judokit.android.inflate
import kotlinx.android.synthetic.main.pbba_button.view.*

class PayByBankButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        inflate(R.layout.pbba_button, true)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        pbbaButton.setOnClickListener(listener)
    }

    override fun setEnabled(enabled: Boolean) {
        pbbaButton.isEnabled = enabled
    }
}
