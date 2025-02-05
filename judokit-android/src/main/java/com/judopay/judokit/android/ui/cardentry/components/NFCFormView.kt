package com.judopay.judokit.android.ui.cardentry.components

import android.graphics.Color
import android.net.Uri
import com.judopay.judokit.android.R

internal typealias NFCFormViewCancelButtonClickListener = () -> Unit

class NFCFormView
    @JvmOverloads
    constructor(
        context: android.content.Context,
        attrs: android.util.AttributeSet? = null,
        defStyle: Int = 0,
    ) : android.widget.FrameLayout(context, attrs, defStyle) {
        val binding = com.judopay.judokit.android.databinding.NfcFormViewBinding.inflate(android.view.LayoutInflater.from(context), this, true)

    internal var onNfcCancelClickListener: NFCFormViewCancelButtonClickListener? =
        null

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding.nfcCancelButton.setOnClickListener {
            hideVideo()
            onNfcCancelClickListener?.invoke()
        }
        setupText()
        setupVideo()
    }

    override fun onAnimationEnd() {
        super.onAnimationEnd()
        showVideo()
    }

    private fun setupText() {
        val nfcText =
            "NFC scanning activated.\n\nHold your card near the back of your mobile device to read its number, name and expiry date."
        binding.nfcTextView.text = nfcText
    }

    private fun setupVideo() {
        binding.videoView.let {
            val videoUri =
                Uri.parse("android.resource://${context.applicationContext.packageName}/${R.raw.nfc}")
            it.setVideoURI(videoUri)
            it.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
            }
            it.start()
        }
    }

    private fun showVideo() {
        binding.videoView.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun hideVideo() {
        binding.videoView.stopPlayback()
        binding.videoView.setBackgroundColor(Color.WHITE)
    }
}
