package com.judopay.ui.cardverification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.judopay.JUDO_RECEIPT
import com.judopay.R
import com.judopay.api.model.response.Receipt
import com.judopay.judo
import com.judopay.ui.cardverification.model.WebViewAction
import kotlinx.android.synthetic.main.card_verification_fragment.cardVerificationWebView
import kotlinx.android.synthetic.main.card_verification_fragment.threeDSProgressBar
import kotlinx.android.synthetic.main.card_verification_fragment.threeDSTextView

interface WebViewCallback {
    fun send(action: WebViewAction)
}

class CardVerificationFragment : Fragment(), WebViewCallback {

    private lateinit var viewModel: CardVerificationViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CardVerificationViewModel::class.java)

        viewModel.receipt.observe(viewLifecycleOwner, Observer {
            with(requireActivity()) {
                setResult(
                    Activity.RESULT_OK,
                    Intent().apply { putExtra(JUDO_RECEIPT, it) })
                finish()
            }
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                threeDSTextView.visibility = View.VISIBLE
                threeDSProgressBar.visibility = View.VISIBLE
            } else {
                threeDSTextView.visibility = View.GONE
                threeDSProgressBar.visibility = View.GONE
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.card_verification_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardVerificationWebView.view = this

        val receipt = arguments?.getParcelable<Receipt>(JUDO_RECEIPT)
        if (receipt != null) {
            cardVerificationWebView.authorize(
                receipt.acsUrl,
                receipt.md,
                receipt.paReq,
                receipt.receiptId
            )
        }
    }

    override fun send(action: WebViewAction) {
        when (action) {
            is WebViewAction.OnPageStarted -> {
                threeDSTextView.visibility = View.VISIBLE
                threeDSProgressBar.visibility = View.VISIBLE
            }
            is WebViewAction.OnPageLoaded -> {
                cardVerificationWebView.visibility = View.VISIBLE
                threeDSTextView.visibility = View.GONE
                threeDSProgressBar.visibility = View.GONE
            }
            is WebViewAction.OnAuthorizationComplete ->
                viewModel.complete3DSecure(judo, action.receiptId, action.cardVerificationResult)
        }
    }
}