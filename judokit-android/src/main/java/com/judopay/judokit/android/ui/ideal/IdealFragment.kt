package com.judopay.judokit.android.ui.ideal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.judopay.judokit.android.JudoSharedViewModel
import com.judopay.judokit.android.animateWithAlpha
import com.judopay.judokit.android.api.error.toJudoError
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.api.model.response.BankSaleStatusResponse
import com.judopay.judokit.android.api.model.response.IdealSaleResponse
import com.judopay.judokit.android.api.model.response.JudoApiCallResult
import com.judopay.judokit.android.api.model.response.toJudoPaymentResult
import com.judopay.judokit.android.api.model.response.toJudoResult
import com.judopay.judokit.android.databinding.IdealFragmentBinding
import com.judopay.judokit.android.judo
import com.judopay.judokit.android.model.JudoError
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.model.JudoResult
import com.judopay.judokit.android.service.polling.PollingResult
import com.judopay.judokit.android.service.polling.PollingService
import com.judopay.judokit.android.ui.common.getLocale
import com.judopay.judokit.android.ui.ideal.components.IdealWebViewCallback
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewState

const val JUDO_IDEAL_BANK = "com.judopay.judokit.android.idealbankbic"
const val BIC_NOT_NULL = "BIC must not be null"

class IdealFragment : Fragment(), IdealWebViewCallback {
    private lateinit var viewModel: IdealViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    private var _binding: IdealFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bic = arguments?.getString(JUDO_IDEAL_BANK)
            ?: throw NullPointerException(BIC_NOT_NULL)

        val application = requireActivity().application
        val service = JudoApiServiceFactory.create(application, judo)
        val pollingService = PollingService(service)
        val factory = IdealViewModelFactory(judo, service, pollingService, application)
        viewModel = ViewModelProvider(this, factory)[IdealViewModel::class.java]

        viewModel.saleCallResult.observe(viewLifecycleOwner) { handleSaleResult(it) }
        viewModel.saleStatusResult.observe(viewLifecycleOwner) { handleSaleStatusResult(it) }

        viewModel.isLoading.observe(
            viewLifecycleOwner
        ) {
            if (it) {
                binding.idealWebView.visibility = View.GONE
                binding.idealPollingStatusView.apply {
                    visibility = View.VISIBLE
                    state = PollingStatusViewState.PROCESSING
                }
            } else {
                binding.idealWebView.visibility = View.VISIBLE
                binding.idealPollingStatusView.visibility = View.GONE
            }
        }

        binding.idealPollingStatusView.animateWithAlpha(1.0f)
        viewModel.send(IdealAction.Initialise(bic))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = IdealFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleSaleResult(result: JudoApiCallResult<IdealSaleResponse>?) {
        when (result) {
            is JudoApiCallResult.Success -> {
                val idealSaleResponse = result.data
                if (idealSaleResponse?.redirectUrl != null && idealSaleResponse.merchantRedirectUrl != null) {
                    binding.idealWebView.authorize(
                        idealSaleResponse.redirectUrl,
                        idealSaleResponse.merchantRedirectUrl
                    )
                } else {
                    sharedViewModel.paymentResult.postValue(
                        JudoPaymentResult.Error(
                            JudoError.judoResponseParseError(resources)
                        )
                    )
                }
            }
            is JudoApiCallResult.Failure -> {
                sharedViewModel.paymentResult.postValue(result.toJudoPaymentResult(resources))
                findNavController().popBackStack()
            }
            null -> {}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener { sharedViewModel.paymentResult.postValue(JudoPaymentResult.UserCancelled()) }

        binding.idealWebView.view = this
    }

    override fun onPageStarted(checksum: String) {
        binding.idealPollingStatusView.visibility = View.VISIBLE
        viewModel.send(IdealAction.StartPolling)
    }

    private fun handleSaleStatusResult(pollingResult: PollingResult<BankSaleStatusResponse>?) {
        when (pollingResult) {
            is PollingResult.Processing -> {
                binding.idealPollingStatusView.state = PollingStatusViewState.PROCESSING
                binding.idealWebView.visibility = View.GONE
            }
            is PollingResult.Delay -> {
                binding.idealPollingStatusView.state = PollingStatusViewState.DELAY
                binding.idealPollingStatusView.binding.pollingButton.setOnClickListener {
                    viewModel.send(IdealAction.RetryPolling)
                }
            }
            is PollingResult.Retry -> {
                binding.idealPollingStatusView.state = PollingStatusViewState.RETRY
                binding.idealPollingStatusView.binding.pollingButton.setOnClickListener {
                    viewModel.send(IdealAction.RetryPolling)
                }
            }
            is PollingResult.Success -> {
                val result = JudoPaymentResult.Success(
                    pollingResult.data?.toJudoResult(getLocale(resources)) ?: JudoResult()
                )
                sharedViewModel.paymentResult.postValue(result)
            }
            is PollingResult.Failure -> {
                val result = JudoPaymentResult.Error(JudoError.judoRequestFailedError(resources))
                sharedViewModel.paymentResult.postValue(result)
                findNavController().popBackStack()
            }
            is PollingResult.CallFailure -> {
                val result = pollingResult.error?.toJudoError() ?: JudoError.judoRequestFailedError(
                    resources
                )
                sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(result))
                findNavController().popBackStack()
            }
            is PollingResult.ResponseParseError -> {
                val error = JudoError.judoResponseParseError(resources)
                val result = JudoPaymentResult.Error(error)
                sharedViewModel.paymentResult.postValue(result)
            }
            null -> {}
        }
    }
}
