package com.judopay.ui.ideal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.judopay.JudoSharedViewModel
import com.judopay.R
import com.judopay.api.factory.JudoApiServiceFactory
import com.judopay.api.model.response.JudoApiCallResult
import com.judopay.api.model.response.toReceipt
import com.judopay.judo
import com.judopay.model.JudoPaymentResult
import com.judopay.ui.ideal.component.IdealWebViewCallback
import kotlinx.android.synthetic.main.ideal_fragment.*

const val JUDO_IDEAL_BANK = "com.judopay.idealbankbic"

class IdealFragment : Fragment(), IdealWebViewCallback {

    private lateinit var viewModel: IdealViewModel
    private val sharedViewModel: JudoSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.ideal_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bic = arguments?.getString(JUDO_IDEAL_BANK)
            ?: throw NullPointerException("BIC must be present")

        val application = requireActivity().application
        val service = JudoApiServiceFactory.createApiService(application, judo)
        val factory = IdealViewModelFactory(bic, judo, service, application)
        viewModel = ViewModelProvider(this, factory).get(IdealViewModel::class.java)

        viewModel.saleCallResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is JudoApiCallResult.Success -> if (it.data != null) {
                    idealWebView.authorize(it.data.redirectUrl, it.data.merchantRedirectUrl)
                }
                is JudoApiCallResult.Failure -> if (it.error != null) {
                    sharedViewModel.idealResult.postValue(JudoPaymentResult.Error(it.error))
                    findNavController().popBackStack()
                }
            }
        })

        viewModel.saleStatusCallResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is JudoApiCallResult.Success -> if (it.data != null) {
                    val locale = ConfigurationCompat.getLocales(resources.configuration)[0]
                    sharedViewModel.idealResult.postValue(
                        JudoPaymentResult.Success(it.data.toReceipt(locale))
                    )
                }
                is JudoApiCallResult.Failure -> if (it.error != null) {
                    sharedViewModel.idealResult.postValue(JudoPaymentResult.Error(it.error))
                }
            }
            findNavController().popBackStack()
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                idealWebView.visibility = View.GONE
                idealTextView.visibility = View.VISIBLE
                idealProgressBar.visibility = View.VISIBLE
            } else {
                idealWebView.visibility = View.VISIBLE
                idealTextView.visibility = View.GONE
                idealProgressBar.visibility = View.GONE
            }
        })

        viewModel.isRequestDelayed.observe(viewLifecycleOwner, Observer {
            if (it) idealTextView.text = getString(R.string.there_is_a_delay)
        })

        viewModel.payWithSelectedBank()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton.setOnClickListener { sharedViewModel.paymentResult.postValue(JudoPaymentResult.UserCancelled) }

        idealWebView.view = this
    }

    override fun onPageStarted(checksum: String) {
        idealTextView.visibility = View.VISIBLE
        idealProgressBar.visibility = View.VISIBLE
        viewModel.completeIdealPayment()
    }
}
