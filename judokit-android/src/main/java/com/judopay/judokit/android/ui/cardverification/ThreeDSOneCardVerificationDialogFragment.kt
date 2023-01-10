package com.judopay.judokit.android.ui.cardverification

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.judopay.judokit.android.R
import com.judopay.judokit.android.api.JudoApiService
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.databinding.ThreeDsOneCardVerificationDialogFragmentBinding
import com.judopay.judokit.android.model.CardVerificationModel
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.ui.cardverification.model.WebViewAction

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
    private var _binding: ThreeDsOneCardVerificationDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Required to make the dialog fullscreen
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val application = requireActivity().application
        val factory = ThreeDSOneCardVerificationViewModelFactory(service, application)
        viewModel = ViewModelProvider(this, factory)[ThreeDSOneCardVerificationViewModel::class.java]

        viewModel.judoApiCallResult.observe(
            viewLifecycleOwner
        ) {
            when (it) {
                is JudoApiCallResult.Success ->
                    completionCallback.onSuccess(it.toJudoPaymentResult(resources))
                is JudoApiCallResult.Failure ->
                    completionCallback.onFailure(it.toJudoPaymentResult(resources))
            }
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ThreeDsOneCardVerificationDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            completionCallback.onFailure(JudoPaymentResult.UserCancelled())
            dismiss()
        }

        with(binding.cardVerificationWebView) {
            this.view = this@ThreeDSOneCardVerificationDialogFragment
            authorize(cardVerificationModel)
        }
    }

    override fun send(action: WebViewAction) {
        when (action) {
            is WebViewAction.OnPageStarted -> {
                binding.threeDSTextView.visibility = View.VISIBLE
                binding.threeDSProgressBar.visibility = View.VISIBLE
            }
            is WebViewAction.OnPageLoaded -> {
                binding.cardVerificationWebView.visibility = View.VISIBLE
                binding.threeDSTextView.visibility = View.GONE
                binding.threeDSProgressBar.visibility = View.GONE
            }
            is WebViewAction.OnAuthorizationComplete -> {
                with(action.cardVerificationResult) {
                    if (md == null) {
                        md = cardVerificationModel.md
                    }
                    viewModel.complete3DSecure(action.receiptId, this)
                }
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
}
