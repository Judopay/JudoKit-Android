package com.judopay.card

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.judopay.Judo
import com.judopay.R
import com.judopay.arch.TextUtil
import com.judopay.arch.ThemeUtil
import com.judopay.model.Card
import com.judopay.validation.ValidationManager.OnChangeListener
import kotlinx.android.synthetic.main.fragment_card_entry.button

abstract class AbstractCardEntryFragment : BottomSheetDialogFragment(),
    OnChangeListener {
    lateinit var cardEntryListener: CardEntryListener
    var buttonLabel: String? = null
        get() {
        if (!TextUtil.isEmpty(field)) {
            return field
        }
        return if (activity != null) {
            ThemeUtil.getStringAttr(activity, R.attr.buttonLabel)
        } else ""
    }
        set(buttonLabel) {
            field = buttonLabel
            setButtonLabelText(buttonLabel)
        }

    protected abstract fun onInitialize(savedInstanceState: Bundle?, judo: Judo)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
            val judo: Judo? = arguments?.getParcelable(Judo.JUDO_OPTIONS)
            if (judo != null) {
                setButtonLabelText(buttonLabel)
                onInitialize(savedInstanceState, judo)
            }
        val touchOutsideView = requireDialog().window?.decorView?.findViewById<View>(R.id.touch_outside)
        touchOutsideView?.setOnClickListener(null)
    }

    private fun setButtonLabelText(buttonLabel: String?) {
        if (!TextUtil.isEmpty(buttonLabel)) {
            button?.getButton()?.text = buttonLabel
        }
    }

    fun hideKeyboard() {
        if (activity != null) {
            val view = requireActivity().currentFocus
            if (view != null) {
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    open fun setCard(card: Card) {}
    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if (activity != null) {
            requireActivity().finish()
        }
    }
}