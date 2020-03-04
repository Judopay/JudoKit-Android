package com.judopay.ui.cardverification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.judopay.JUDO_RECEIPT
import com.judopay.R
import com.judopay.api.model.response.Receipt
import kotlinx.android.synthetic.main.card_verification_fragment.cardVerificationWebView

class CardVerificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.card_verification_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardVerificationWebView.setListeners({
            //on page started
        }, {
            //on page loaded
        }, { cardVerificationResult, receiptId ->
            //apiService.complete3dSecure(receiptId, cardVerificationResult)
        })
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
}