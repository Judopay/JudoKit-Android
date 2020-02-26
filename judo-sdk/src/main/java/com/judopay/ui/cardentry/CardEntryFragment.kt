package com.judopay.ui.cardentry

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judopay.R
import com.judopay.ui.cardentry.components.*
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

class CardEntryFragment : BottomSheetDialogFragment() {

    private lateinit var simpleKeyboardAnimator: SimpleKeyboardAnimator

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

    private fun buildFormViewModel(): FormViewModel {
        return FormViewModel(
                FormModel(
                        "1234   5678   9012   3456",
                        "Endava Recruit",
                        "11/20",
                        "123",
                        "Country",
                        "Postcode"
                ),
                listOf(FormFieldType.NUMBER, FormFieldType.HOLDER_NAME, FormFieldType.EXPIRATION_DATE, FormFieldType.SECURITY_NUMBER),
                mapOf(
                        Pair(R.id.numberTextInputEditText, InputFieldConfiguration(FormFieldType.NUMBER)),
                        Pair(R.id.nameTextInputEditText, InputFieldConfiguration(FormFieldType.HOLDER_NAME)),
                        Pair(R.id.expirationDateTextInputEditText, InputFieldConfiguration(FormFieldType.EXPIRATION_DATE)),
                        Pair(R.id.securityNumberTextInputEditText, InputFieldConfiguration(FormFieldType.SECURITY_NUMBER)),
                        Pair(R.id.countryTextInputEditText, InputFieldConfiguration(FormFieldType.COUNTRY)),
                        Pair(R.id.postcodeTextInputEditText, InputFieldConfiguration(FormFieldType.POST_CODE)),
                        Pair(R.id.submitButton, SubmitFieldConfiguration(FormFieldType.SUBMIT, "Add card"))
                )
        )
    }
}