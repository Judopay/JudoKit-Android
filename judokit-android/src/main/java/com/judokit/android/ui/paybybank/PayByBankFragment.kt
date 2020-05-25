package com.judokit.android.ui.paybybank

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.judokit.android.JudoSharedViewModel
import com.judokit.android.R
import com.judokit.android.api.factory.JudoApiServiceFactory
import com.judokit.android.judo
import com.judokit.android.service.polling.PollingService
import com.judokit.android.ui.ideal.IdealViewModel

class PayByBankFragment : Fragment() {
    private lateinit var viewModel: IdealViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.polling_status_view, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        val service = JudoApiServiceFactory.createApiService(application, judo)
        val pollingService = PollingService(service)
        val factory = PayByBankViewModel(service, pollingService, application,judo)
        viewModel = ViewModelProvider(this, factory).get(PayByBankViewModel::class.java)

    }
}