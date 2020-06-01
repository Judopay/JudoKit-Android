package com.judokit.android.ui.pollingstatus

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.judokit.android.JudoSharedViewModel
import com.judokit.android.R
import com.judokit.android.api.error.toJudoError
import com.judokit.android.api.factory.JudoApiServiceFactory
import com.judokit.android.api.model.response.BankSaleResponse
import com.judokit.android.api.model.response.BankSaleStatusResponse
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.toJudoPaymentResult
import com.judokit.android.api.model.response.toJudoResult
import com.judokit.android.judo
import com.judokit.android.model.JudoError
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.model.JudoResult
import com.judokit.android.service.polling.PollingResult
import com.judokit.android.service.polling.PollingService
import com.judokit.android.ui.common.getLocale
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewAction
import com.judokit.android.ui.paymentmethods.components.PollingStatusViewState
import com.zapp.library.merchant.ui.PBBAPopupCallback
import com.zapp.library.merchant.util.PBBAAppUtils
import kotlinx.android.synthetic.main.polling_status_fragment.*

class PollingStatusFragment : DialogFragment() {

    private lateinit var viewModel: PollingStatusViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    private var bankOrderId: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                sharedViewModel.bankPaymentResult.postValue(JudoPaymentResult.UserCancelled())
                super.onBackPressed()
            }
        }
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
            setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.polling_status_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        val service = JudoApiServiceFactory.createApiService(application, judo)
        val pollingService = PollingService(service)
        val factory = PollingStatusViewModelFactory(service, pollingService, application, judo)
        viewModel = ViewModelProvider(this, factory).get(PollingStatusViewModel::class.java)

        viewModel.send(PollingAction.PayWithPayByBank)

        viewModel.payByBankResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is JudoApiCallResult.Success -> handleBankSaleResponse(it.data)
                is JudoApiCallResult.Failure -> {
                    sharedViewModel.bankPaymentResult.postValue(it.toJudoPaymentResult())
                    findNavController().popBackStack()
                }
            }
        })
        viewModel.saleStatusResult.observe(viewLifecycleOwner, Observer {
            handleBankResult(it)
        })
        pollingStatusView.onButtonClickListener = { handlePollingStatusViewButtonClick(it) }
    }

    private fun handleBankSaleResponse(data: BankSaleResponse?) {
        if (data != null) {
            PBBAAppUtils.showPBBAPopup(
                requireActivity(),
                data.secureToken,
                data.pbbaBrn,
                object : PBBAPopupCallback {
                    override fun onRetryPaymentRequest() {
                        // noop
                    }

                    override fun onDismissPopup() {
                        // noop
                    }
                }
            )
            bankOrderId = data.orderId
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
                        sharedViewModel.bankPaymentResult.postValue(result)
                    }
                    else -> {
                        viewModel.send(PollingAction.CancelPolling)
                        sharedViewModel.bankPaymentResult.postValue(result)
                    }
                }
            }
        }
    }

    var result: JudoPaymentResult = JudoPaymentResult.UserCancelled()
    private fun handleBankResult(pollingResult: PollingResult<BankSaleStatusResponse>?) {
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
                val error = pollingResult.error?.toJudoError() ?: JudoError.generic()
                sharedViewModel.bankPaymentResult.postValue(JudoPaymentResult.Error(error))
                PollingStatusViewState.FAIL
            }
            else -> null
        }
    }

    override fun onResume() {
        super.onResume()
        if (!bankOrderId.isNullOrEmpty()) {
            requireDialog().window?.apply {
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setDimAmount(0.5f)
            }
            pollingStatusView.visibility = View.VISIBLE
            viewModel.send(PollingAction.StartPolling(bankOrderId!!))
            bankOrderId = null
        }
    }
}
