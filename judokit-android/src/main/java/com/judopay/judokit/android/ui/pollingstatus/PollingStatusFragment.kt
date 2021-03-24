package com.judopay.judokit.android.ui.pollingstatus

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.judopay.judokit.android.JudoSharedViewModel
import com.judopay.judokit.android.R
import com.judopay.judokit.android.animateWithAlpha
import com.judopay.judokit.android.api.error.toJudoError
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.model.response.BankSaleResponse
import com.judopay.judokit.android.api.model.response.BankSaleStatusResponse
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.api.model.response.toJudoResult
import com.judopay.judokit.android.applyDialogStyling
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
import com.judopay.judokit.android.ui.paymentmethods.PAYMENT_WIDGET_TYPE
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewAction
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewState
import com.zapp.library.merchant.ui.PBBAPopupCallback
import com.zapp.library.merchant.util.PBBAAppUtils
import kotlinx.android.synthetic.main.polling_status_fragment.*

class PollingStatusFragment : DialogFragment(), PBBAPopupCallback {

    private lateinit var viewModel: PollingStatusViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    var result: JudoPaymentResult = JudoPaymentResult.UserCancelled()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.polling_status_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val paymentWidgetType = arguments?.getParcelable<PaymentWidgetType>(PAYMENT_WIDGET_TYPE)

        val application = requireActivity().application
        val service = JudoApiServiceFactory.createApiService(application, judo)
        val pollingService = PollingService(service)
        val factory = PollingStatusViewModelFactory(
            service,
            pollingService,
            application,
            judo,
            paymentWidgetType
        )
        viewModel = ViewModelProvider(this, factory).get(PollingStatusViewModel::class.java)

        viewModel.send(PollingAction.Initialise(sharedViewModel.error.details.isEmpty()))

        viewModel.payByBankResult.observe(
            viewLifecycleOwner,
            { result ->
                handlePayByBankResult(result)
            }
        )
        viewModel.saleStatusResult.observe(
            viewLifecycleOwner,
            { result ->
                handleSaleStatusResult(result)
            }
        )
        pollingStatusView.onButtonClickListener = { handlePollingStatusViewButtonClick(it) }

        pollingStatusView.animateWithAlpha(1.0f)
    }

    // PBBAPopupCallback
    override fun onRetryPaymentRequest() {
        viewModel.send(PollingAction.RetryPolling)
    }

    override fun onDismissPopup() {
        viewModel.send(PollingAction.CancelPolling)
    }

    private fun handlePayByBankResult(result: JudoApiCallResult<BankSaleResponse>?) {
        when (result) {
            is JudoApiCallResult.Success -> handleBankSaleResponse(result)
            is JudoApiCallResult.Failure -> {
                sharedViewModel.bankPaymentResult.postValue(result.toJudoPaymentResult(resources))
                findNavController().popBackStack()
            }
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
                when (pollingStatusView.state) {
                    PollingStatusViewState.DELAY -> viewModel.send(PollingAction.ResetPolling)
                    PollingStatusViewState.RETRY -> viewModel.send(PollingAction.RetryPolling)
                    else -> {
                        // noop
                    }
                }

                pollingStatusView.state = PollingStatusViewState.PROCESSING
            }

            PollingStatusViewAction.CLOSE -> {
                when (pollingStatusView.state) {
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
        pollingStatusView.state = when (pollingResult) {
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
