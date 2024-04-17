package com.judopay.judokit.android.ui.pollingstatus

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.judopay.judokit.android.JudoSharedViewModel
import com.judopay.judokit.android.animateWithAlpha
import com.judopay.judokit.android.api.factory.JudoApiServiceFactory
import com.judopay.judokit.android.applyDialogStyling
import com.judopay.judokit.android.databinding.PollingStatusFragmentBinding
import com.judopay.judokit.android.judo
import com.judopay.judokit.android.model.JudoPaymentResult
import com.judopay.judokit.android.service.polling.PollingService
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewAction
import com.judopay.judokit.android.ui.paymentmethods.components.PollingStatusViewState

class PollingStatusFragment : DialogFragment() {
    private lateinit var viewModel: PollingStatusViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()
    var result: JudoPaymentResult = JudoPaymentResult.UserCancelled()
    private var _binding: PollingStatusFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =
            object : Dialog(requireContext(), theme) {
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
        savedInstanceState: Bundle?,
    ): View {
        _binding = PollingStatusFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        val service = JudoApiServiceFactory.create(application, judo)
        val pollingService = PollingService(service)
        val factory = PollingStatusViewModelFactory(pollingService, application)
        viewModel = ViewModelProvider(this, factory)[PollingStatusViewModel::class.java]

        binding.pollingStatusView.onButtonClickListener = { handlePollingStatusViewButtonClick(it) }

        binding.pollingStatusView.animateWithAlpha(1.0f)
    }

    private fun handlePollingStatusViewButtonClick(action: PollingStatusViewAction) {
        when (action) {
            PollingStatusViewAction.RETRY -> {
                when (binding.pollingStatusView.state) {
                    PollingStatusViewState.DELAY -> viewModel.send(PollingAction.ResetPolling)
                    PollingStatusViewState.RETRY -> viewModel.send(PollingAction.RetryPolling)
                    else -> {}
                }

                binding.pollingStatusView.state = PollingStatusViewState.PROCESSING
            }

            PollingStatusViewAction.CLOSE -> {
                when (binding.pollingStatusView.state) {
                    PollingStatusViewState.FAIL,
                    PollingStatusViewState.SUCCESS,
                    -> {
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
}
