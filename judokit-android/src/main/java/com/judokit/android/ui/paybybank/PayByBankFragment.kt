package com.judokit.android.ui.paybybank

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.pay_by_bank_fragment.*

class PayByBankFragment : DialogFragment() {

    private lateinit var viewModel: PayByBankViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    private var bankOrderId: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
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
    ): View? = inflater.inflate(R.layout.pay_by_bank_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        val service = JudoApiServiceFactory.createApiService(application, judo)
        val pollingService = PollingService(service)
        val factory = PayByBankViewModelFactory(service, pollingService, application, judo)
        viewModel = ViewModelProvider(this, factory).get(PayByBankViewModel::class.java)

        viewModel.payByBankResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is JudoApiCallResult.Success -> handleBankSaleResponse(it.data)
                is JudoApiCallResult.Failure -> {
                    sharedViewModel.paymentResult.postValue(it.toJudoPaymentResult())
                    findNavController().popBackStack()
                }
            }
        })
        viewModel.payByBankStatusResult.observe(viewLifecycleOwner, Observer {
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
            sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(JudoError.generic()))
            findNavController().popBackStack()
        }
    }

    private fun handlePollingStatusViewButtonClick(action: PollingStatusViewAction) {
        when (action) {
            PollingStatusViewAction.RETRY -> {
                when (pollingStatusView.state) {
                    PollingStatusViewState.DELAY -> viewModel.send(PayByBankAction.ResetBankPolling)
                    PollingStatusViewState.RETRY -> viewModel.send(PayByBankAction.RetryBankPolling)
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
                        sharedViewModel.paymentResult.postValue(result)
                    }
                    else -> {
                        viewModel.send(PayByBankAction.CancelBankPayment)
                        sharedViewModel.paymentResult.postValue(result)
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
                sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(error))
                PollingStatusViewState.FAIL
            }
            else -> null
        }
    }

    override fun onResume() {
        super.onResume()
        if (!bankOrderId.isNullOrEmpty()) {
            pollingStatusView.visibility = View.VISIBLE
            viewModel.send(PayByBankAction.StartPolling(bankOrderId!!))
            bankOrderId = null
        }
    }
}
