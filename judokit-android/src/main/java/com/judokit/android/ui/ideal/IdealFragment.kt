package com.judokit.android.ui.ideal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.judokit.android.JudoSharedViewModel
import com.judokit.android.R
import com.judokit.android.api.error.toJudoError
import com.judokit.android.api.factory.JudoApiServiceFactory
import com.judokit.android.api.model.response.JudoApiCallResult
import com.judokit.android.api.model.response.toJudoResult
import com.judokit.android.judo
import com.judokit.android.model.JudoPaymentResult
import com.judokit.android.ui.common.getLocale
import com.judokit.android.ui.ideal.components.IdealWebViewCallback
import kotlinx.android.synthetic.main.ideal_fragment.*

const val JUDO_IDEAL_BANK = "com.judokit.android.idealbankbic"
const val BIC_NOT_NULL = "BIC must not be null"

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
            ?: throw NullPointerException(BIC_NOT_NULL)

        val application = requireActivity().application
        val service = JudoApiServiceFactory.createApiService(application, judo)
        val factory = IdealViewModelFactory(bic, judo, service, application)
        viewModel = ViewModelProvider(this, factory).get(IdealViewModel::class.java)

        viewModel.saleCallResult.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is JudoApiCallResult.Success -> if (it.data != null) {
                        idealWebView.authorize(it.data.redirectUrl, it.data.merchantRedirectUrl)
                    }
                    is JudoApiCallResult.Failure -> if (it.error != null) {
                        sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(it.error.toJudoError()))
                        findNavController().popBackStack()
                    }
                }
            }
        )

        viewModel.saleStatusCallResult.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is JudoApiCallResult.Success -> if (it.data != null) {
                        val locale = getLocale(resources)
                        sharedViewModel.paymentResult.postValue(
                            JudoPaymentResult.Success(it.data.toJudoResult(locale))
                        )
                    }
                    is JudoApiCallResult.Failure -> if (it.error != null) {
                        sharedViewModel.paymentResult.postValue(JudoPaymentResult.Error(it.error.toJudoError()))
                    }
                }
                findNavController().popBackStack()
            }
        )

        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer {
                if (it) {
                    idealWebView.visibility = View.GONE
                    idealTextView.visibility = View.VISIBLE
                    idealProgressBar.visibility = View.VISIBLE
                } else {
                    idealWebView.visibility = View.VISIBLE
                    idealTextView.visibility = View.GONE
                    idealProgressBar.visibility = View.GONE
                }
            }
        )

        viewModel.isRequestDelayed.observe(
            viewLifecycleOwner,
            Observer {
                if (it) idealTextView.text = getString(R.string.there_is_a_delay)
            }
        )

        viewModel.payWithSelectedBank()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton.setOnClickListener { sharedViewModel.paymentResult.postValue(JudoPaymentResult.UserCancelled()) }

        idealWebView.view = this
    }

    override fun onPageStarted(checksum: String) {
        idealTextView.visibility = View.VISIBLE
        idealProgressBar.visibility = View.VISIBLE
        viewModel.completeIdealPayment()
    }
}
