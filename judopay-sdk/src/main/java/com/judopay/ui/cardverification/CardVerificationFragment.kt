package com.judopay.ui.cardverification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.judopay.JUDO_RECEIPT
import com.judopay.JudoSharedViewModel
import com.judopay.R
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.Receipt
import com.judopay.judo
import com.judopay.model.JudoPaymentResult
import com.judopay.ui.cardverification.model.WebViewAction
import kotlinx.android.synthetic.main.card_verification_fragment.*

interface WebViewCallback {
    fun send(action: WebViewAction)
}

class CardVerificationFragment : Fragment(), WebViewCallback {

    private lateinit var viewModel: CardVerificationViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CardVerificationViewModel::class.java)

        viewModel.judoApiCallResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is JudoApiCallResult.Success -> if (it.data != null) {
                    sharedViewModel.threeDSecureResult.postValue(JudoPaymentResult.Success(it.data))
                }
                is JudoApiCallResult.Failure -> if (it.error != null) {
                    sharedViewModel.threeDSecureResult.postValue(JudoPaymentResult.Error(it.error))
                }
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

        backButton.setOnClickListener { sharedViewModel.paymentResult.postValue(JudoPaymentResult.UserCancelled) }

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
