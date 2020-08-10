package com.judokit.android.ui.pollingstatus

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.judokit.android.JudoSharedViewModel
import com.judokit.android.R
import com.judokit.android.animateWithAlpha
import com.judokit.android.api.error.toJudoError
import com.judokit.android.api.factory.JudoApiServiceFactory
import com.judokit.android.api.model.response.BankSaleResponse
import com.judokit.android.api.model.response.BankSaleStatusResponse
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.toJudoPaymentResult
import com.judokit.android.api.model.response.toJudoResult
import com.judokit.android.applyDialogStyling
import com.judokit.android.judo
import com.judokit.android.model.JudoError
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.model.JudoResult
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.service.polling.PollingResult
import com.judokit.android.service.polling.PollingService
import com.judokit.android.ui.common.getLocale
import com.judokit.android.ui.paymentmethods.PAYMENT_WIDGET_TYPE
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewAction
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewState
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
                sharedViewModel.bankPaymentResult.postValue(JudoPaymentResult.UserCancelled())
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
            Observer { result ->
                handlePayByBankResult(result)
            }
        )
        viewModel.saleStatusResult.observe(
            viewLifecycleOwner,
            Observer { result ->
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
            is JudoApiCallResult.Success -> handleBankSaleResponse(result.data)
            is JudoApiCallResult.Failure -> {
                sharedViewModel.bankPaymentResult.postValue(result.toJudoPaymentResult())
                findNavController().popBackStack()
            }
        }
    }

    private fun handleBankSaleResponse(data: BankSaleResponse?) {
        if (data != null) {
            PBBAAppUtils.showPBBAPopup(
                requireActivity(),
                data.secureToken,
                data.pbbaBrn,
                this
            )
        } else {
            sharedViewModel.bankPaymentResult.postValue(JudoPaymentResult.Error(JudoError.generic()))
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
                result = JudoPaymentResult.Error(JudoError.generic())
                PollingStatusViewState.FAIL
            }
            is PollingResult.CallFailure -> {
                val error = pollingResult.error?.toJudoError() ?: JudoError.generic()
                result = JudoPaymentResult.Error(error)
                PollingStatusViewState.FAIL
            }
            else -> null
        }
    }
}
