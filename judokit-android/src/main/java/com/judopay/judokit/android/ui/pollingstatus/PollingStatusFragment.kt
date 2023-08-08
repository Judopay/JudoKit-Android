package com.judopay.judokit.android.ui.pollingstatus

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.judopay.judokit.android.JudoSharedViewModel
import com.judopay.judokit.android.animateWithAlpha
import com.judopay.judokit.android.api.error.toJudoError
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.model.response.BankSaleResponse
import com.judopay.judokit.android.api.model.response.BankSaleStatusResponse
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.api.model.response.toJudoResult
import com.judopay.judokit.android.applyDialogStyling
import com.judopay.judokit.android.databinding.PollingStatusFragmentBinding
import com.judopay.judokit.android.judo
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.model.PaymentWidgetType
import com.judopay.judokit.android.service.polling.PollingResult
import com.judopay.judokit.android.service.polling.PollingService
import com.judopay.judokit.android.ui.common.BR_PBBA_RESULT
import com.judopay.judokit.android.ui.common.PBBA_RESULT
import com.judopay.judokit.android.ui.common.getLocale
import com.judopay.judokit.android.ui.common.parcelable
import com.judopay.judokit.android.ui.paymentmethods.PAYMENT_WIDGET_TYPE
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewAction
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewState
import com.zapp.library.merchant.ui.PBBAPopupCallback
import com.zapp.library.merchant.util.PBBAAppUtils

class PollingStatusFragment : DialogFragment(), PBBAPopupCallback {
    private lateinit var viewModel: PollingStatusViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    var result: JudoPaymentResult = JudoPaymentResult.UserCancelled()
    private var _binding: PollingStatusFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                sharedViewModel.bankPaymentResult.postValue(result)
                super.onBackPressed()
            }
        }
        dialog.window?.applyDialogStyling()
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PollingStatusFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val paymentWidgetType = arguments?.parcelable<PaymentWidgetType>(PAYMENT_WIDGET_TYPE)

        val application = requireActivity().application
        val service = JudoApiServiceFactory.createJudoApiService(application, judo)
        val pollingService = PollingService(service)
        val factory = PollingStatusViewModelFactory(
            service,
            pollingService,
            application,
            judo,
            paymentWidgetType
        )
        viewModel = ViewModelProvider(this, factory)[PollingStatusViewModel::class.java]

        viewModel.send(PollingAction.Initialise(sharedViewModel.error.details.isEmpty()))

        viewModel.payByBankResult.observe(
            viewLifecycleOwner
        ) { result ->
            handlePayByBankResult(result)
        }
        viewModel.saleStatusResult.observe(
            viewLifecycleOwner
        ) { result ->
            handleSaleStatusResult(result)
        }
        binding.pollingStatusView.onButtonClickListener = { handlePollingStatusViewButtonClick(it) }

        binding.pollingStatusView.animateWithAlpha(1.0f)
    }

    // PBBAPopupCallback
    override fun onRetryPaymentRequest() {
        viewModel.send(PollingAction.RetryPolling)
    }

    override fun onDismissPopup() {
        viewModel.send(PollingAction.CancelPolling)
    }

    override fun onStartTimer() {
    }

    override fun onEndTimer() {
    }

    private fun handlePayByBankResult(result: JudoApiCallResult<BankSaleResponse>?) {
        when (result) {
            is JudoApiCallResult.Success -> handleBankSaleResponse(result)
            is JudoApiCallResult.Failure -> {
                sharedViewModel.bankPaymentResult.postValue(result.toJudoPaymentResult(resources))
                findNavController().popBackStack()
            }
            null -> {}
        }
    }

    private fun handleBankSaleResponse(result: JudoApiCallResult.Success<BankSaleResponse>?) {
        val data = result?.data
        if (data?.secureToken != null) {
            sharedViewModel.bankPaymentResult.postValue(JudoPaymentResult.Success(data.toJudoResult()))
            val intent = Intent(BR_PBBA_RESULT)
            intent.putExtra(
                PBBA_RESULT,
                data.toJudoResult()
            )
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            PBBAAppUtils.openBankingApp(requireActivity(), data.secureToken)
        } else {
            sharedViewModel.bankPaymentResult.postValue(JudoPaymentResult.Error(JudoError.judoResponseParseError(resources)))
            findNavController().popBackStack()
        }
    }

    private fun handlePollingStatusViewButtonClick(action: PollingStatusViewAction) {
        when (action) {
            PollingStatusViewAction.RETRY -> {
                when (binding.pollingStatusView.state) {
                    PollingStatusViewState.DELAY -> viewModel.send(PollingAction.ResetPolling)
                    PollingStatusViewState.RETRY -> viewModel.send(PollingAction.RetryPolling)
                    else -> {
                        // noop
                    }
                }

                binding.pollingStatusView.state = PollingStatusViewState.PROCESSING
            }

            PollingStatusViewAction.CLOSE -> {
                when (binding.pollingStatusView.state) {
                    PollingStatusViewState.FAIL,
                    PollingStatusViewState.SUCCESS -> {
                        findNavController().popBackStack()
                        sharedViewModel.bankPaymentResult.postValue(result)
                    }
                    else -> {
                        viewModel.send(PollingAction.CancelPolling)
                        findNavController().popBackStack()
                        sharedViewModel.bankPaymentResult.postValue(result)
                    }
                }
            }
        }
    }

    private fun handleSaleStatusResult(pollingResult: PollingResult<BankSaleStatusResponse>?) {
        binding.pollingStatusView.state = when (pollingResult) {
            is PollingResult.Processing -> PollingStatusViewState.PROCESSING
            is PollingResult.Delay -> PollingStatusViewState.DELAY
            is PollingResult.Retry -> PollingStatusViewState.RETRY
            is PollingResult.Success -> {
                result = JudoPaymentResult.Success(
                    pollingResult.data?.toJudoResult(getLocale(resources)) ?: JudoResult()
                )
                PollingStatusViewState.SUCCESS
            }
            is PollingResult.Failure -> {
                result = JudoPaymentResult.Error(JudoError.judoRequestFailedError(resources))
                PollingStatusViewState.FAIL
            }
            is PollingResult.CallFailure -> {
                val error = pollingResult.error?.toJudoError() ?: JudoError.judoRequestFailedError(resources)
                result = JudoPaymentResult.Error(error)
                PollingStatusViewState.FAIL
            }
            is PollingResult.ResponseParseError -> {
                val error = JudoError.judoResponseParseError(resources)
                result = JudoPaymentResult.Error(error)
                PollingStatusViewState.FAIL
            }
            else -> null
        }
    }
}
