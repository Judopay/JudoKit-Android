package com.judopay.ui.cardentry

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judopay.R
import com.judopay.api.model.request.Address
import com.judopay.api.model.request.SaveCardRequest
import com.judopay.api.model.response.Receipt
import com.judopay.judo
import com.judopay.ui.cardentry.components.FormFieldType
import com.judopay.ui.cardentry.components.FormView
import com.judopay.ui.cardentry.components.FormViewModel
import com.judopay.ui.cardentry.model.FormModel
import kotlinx.android.synthetic.main.card_entry_fragment.*

class SimpleKeyboardAnimator(private val window: Window?) {

    init {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private val insetsListener: View.OnApplyWindowInsetsListener
        get() = View.OnApplyWindowInsetsListener { view, insets ->
            sceneRoot?.let { TransitionManager.beginDelayedTransition(it, ChangeBounds()) }
            return@OnApplyWindowInsetsListener if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.onApplyWindowInsets(insets)
            } else {
                return@OnApplyWindowInsetsListener null
            }
        }

    private val sceneRoot: ViewGroup? by lazy(LazyThreadSafetyMode.NONE) {
        window?.decorView?.findViewById<View>(Window.ID_ANDROID_CONTENT)?.parent as? ViewGroup
    }

    fun setListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.decorView?.setOnApplyWindowInsetsListener(insetsListener)
        }
    }

    fun removeListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.decorView?.setOnApplyWindowInsetsListener(null)
        }
    }
}

class CardEntryFragment(
        internal val callback: OnResultListener
) : BottomSheetDialogFragment(), FormView.OnSubmitListener {

    private lateinit var viewModel: CardEntryViewModel

    interface OnResultListener {
        fun onResult(fragment: CardEntryFragment, response: Receipt)
    }

    private lateinit var simpleKeyboardAnimator: SimpleKeyboardAnimator

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CardEntryViewModel::class.java)
    }

    override fun getTheme(): Int = R.style.JudoTheme_BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme).apply {
        setOnShowListener {
            Log.d("CardEntryFragment", "setOnShowListener")
            val d = dialog as BottomSheetDialog

            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!).setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.card_entry_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelButton.setOnClickListener(this::handleCancelButtonClicks)
        scanCardButton.setOnClickListener(this::handleScanCardButtonClicks)

        formView.model = buildFormViewModel()
        formView.onSubmitListener = this
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Log.d("CardEntryFragment", "setOnCancelListener")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("CardEntryFragment", "setOnDismissListener")
    }

    private fun handleCancelButtonClicks(view: View) {
        view.isEnabled = false
        dismiss()
    }

    private fun handleScanCardButtonClicks(view: View) {
        Toast.makeText(requireContext(), "handleScanCardButtonClicks", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        simpleKeyboardAnimator = SimpleKeyboardAnimator(requireDialog().window)
                .apply { setListener() }
    }

    override fun onStop() {
        super.onStop()
        simpleKeyboardAnimator.removeListener()
    }

    override fun onSubmitForm(form: FormView, model: FormModel) {
        val request = SaveCardRequest.Builder()
                .setUniqueRequest(false)
                .setYourPaymentReference(judo.reference.paymentReference)
                .setAmount(judo.amount.amount)
                .setCurrency(judo.amount.currency.name)
                .setJudoId(judo.judoId)
                .setYourConsumerReference(judo.reference.consumerReference)
                .setYourPaymentMetaData(emptyMap())
                .setAddress(Address.Builder().build())
                .setCardNumber(model.cardNumber)
                .setExpiryDate(model.expirationDate)
                .setCv2(model.securityNumber)
                .build()

        viewModel.send(judo, request).observe(this, Observer {
            it?.let {
                callback.onResult(this, it)
            }
        })
    }

    private fun buildFormViewModel(): FormViewModel {
        return FormViewModel(
                // Model to pre fil the form
                FormModel(),

                // Fields to show in form
                listOf(FormFieldType.NUMBER, FormFieldType.HOLDER_NAME, FormFieldType.EXPIRATION_DATE, FormFieldType.SECURITY_NUMBER),

                // Supported networks
                judo.supportedCardNetworks.toList()
        )
    }
}