package com.judopay.judokit.android.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.judopay.judokit.android.databinding.PbbaButtonBinding

class PayByBankButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    private val binding = PbbaButtonBinding.inflate(LayoutInflater.from(context), this, true)

    override fun setOnClickListener(listener: OnClickListener?) {
        binding.pbbaButton.setOnClickListener(listener)
    }

    override fun setEnabled(enabled: Boolean) {
        binding.pbbaButton.isEnabled = enabled
    }
}
