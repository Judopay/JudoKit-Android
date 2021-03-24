package com.judopay.judokit.android.ui.cardverification

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.error.toJudoError
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.toJudoResult
import com.judopay.judokit.android.model.CardVerificationModel
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.ui.cardverification.model.WebViewAction
import kotlinx.android.synthetic.main.three_ds_one_card_verification_dialog_fragment.*

/**
 * Tag associated to the [ThreeDSOneCardVerificationDialogFragment] which is passed through
 * [ThreeDSOneCardVerificationDialogFragment.show] method along with FragmentManager.
 */
const val THREE_DS_ONE_DIALOG_FRAGMENT_TAG = "JUDO_THREE_DS_ONE_DIALOG_FRAGMENT"

/**
 * A callback interface that notifies the caller with a given [WebViewAction].
 */
internal interface WebViewCallback {
    fun send(action: WebViewAction)
}

/**
 * A callback interface that notifies the caller with a success or failure transaction result.
 */
interface ThreeDSOneCompletionCallback {
    fun onSuccess(success: JudoPaymentResult)
    fun onFailure(error: JudoPaymentResult)
}

/**
 * Dialog fragment that can be invoked from anywhere given the caller has access to FragmentManager
 * via the show method.
 * @param service An instance of [JudoApiService] created through [com.judopay.judokit.android.api.factory.JudoApiServiceFactory].
 * @param cardVerificationModel A model that contains all the necessary parameters to pass 3DS authentication.
 * The parameters are obtained from [com.judopay.judokit.android.api.model.response.Receipt] model.
 * @param completionCallback Callback with success or failure responses from 3DS authentication.
 */
class ThreeDSOneCardVerificationDialogFragment constructor(
    private val service: JudoApiService,
    private val cardVerificationModel: CardVerificationModel,
    private val completionCallback: ThreeDSOneCompletionCallback
) : DialogFragment(), WebViewCallback {

    private lateinit var viewModel: ThreeDSOneCardVerificationViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Required to make the dialog fullscreen
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val application = requireActivity().application
        val factory = ThreeDSOneCardVerificationViewModelFactory(service, application)
        viewModel =
            ViewModelProvider(this, factory).get(ThreeDSOneCardVerificationViewModel::class.java)

        viewModel.judoApiCallResult.observe(
            viewLifecycleOwner,
            {
                when (it) {
                    is JudoApiCallResult.Success -> if (it.data != null) {
                        completionCallback.onSuccess(JudoPaymentResult.Success(it.data.toJudoResult()))
                    }
                    is JudoApiCallResult.Failure -> if (it.error != null) {
                        completionCallback.onFailure(JudoPaymentResult.Error(it.error.toJudoError()))
                    }
                }
                dismiss()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton.setOnClickListener {
            completionCallback.onFailure(JudoPaymentResult.UserCancelled())
            dismiss()
        }
        with(cardVerificationWebView) {
            this.view = this@ThreeDSOneCardVerificationDialogFragment
            authorize(cardVerificationModel)
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
            is WebViewAction.OnAuthorizationComplete -> {
                viewModel.complete3DSecure(action.receiptId, action.cardVerificationResult)
            }
        }
    }

    override fun getTheme(): Int = R.style.JudoTheme_FullscreenDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                completionCallback.onFailure((JudoPaymentResult.UserCancelled()))
                super.onBackPressed()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.three_ds_one_card_verification_dialog_fragment, container, false)
}
