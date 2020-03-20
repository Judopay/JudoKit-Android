package com.judopay.ui.editcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.judopay.R
import com.judopay.db.entity.TokenizedCardEntity
import com.judopay.ui.editcard.adapter.ColorPickerAdapter
import com.judopay.ui.editcard.adapter.ColorPickerItem
import com.judopay.ui.paymentmethods.model.PaymentCardViewModel
import kotlinx.android.synthetic.main.edit_card_fragment.backButton
import kotlinx.android.synthetic.main.edit_card_fragment.cancelButton
import kotlinx.android.synthetic.main.edit_card_fragment.cardView
import kotlinx.android.synthetic.main.edit_card_fragment.colorPickerRecyclerView
import kotlinx.android.synthetic.main.edit_card_fragment.saveAsDefaultTextView
import kotlinx.android.synthetic.main.edit_card_fragment.saveButton
import kotlinx.android.synthetic.main.edit_card_fragment.titleEditText
import kotlinx.android.synthetic.main.edit_card_fragment.titleTextInputLayout

const val JUDO_TOKENIZED_CARD_ID = "com.judopay.judo-tokenized-card-id"

data class EditCardModel(
    val colorOptions: List<ColorPickerItem>,
    val isSaveButtonEnabled: Boolean,
    val card: PaymentCardViewModel,
    var title: String,
    val isDefault: Boolean
)

private const val CARD_TITLE_MAX_CHARACTERS = 28
class EditCardFragment : Fragment() {

    private lateinit var viewModel: EditCardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_card_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val cardId = arguments?.getInt(JUDO_TOKENIZED_CARD_ID) ?: -1

        val application = requireActivity().application
        val factory = EditCardViewModelFactory(application, cardId)

        viewModel = ViewModelProvider(this, factory).get(EditCardViewModel::class.java)
        viewModel.model.observe(viewLifecycleOwner, Observer { updateWithModel(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupColorPicker()
        setupCallbackListeners()
    }

    private fun updateWithModel(model: EditCardModel) {
        // setup colors
        (colorPickerRecyclerView.adapter as? ColorPickerAdapter)?.items = model.colorOptions

        saveButton.isEnabled = model.isSaveButtonEnabled

        // TODO: temp hack
        titleEditText.apply {
            setText(model.title)
            setSelection(text.length)
        }

        val checkMark = if (model.isDefault) R.drawable.ic_radio_on else R.drawable.ic_radio_off
        saveAsDefaultTextView.setCompoundDrawablesWithIntrinsicBounds(checkMark, 0, 0, 0)

        cardView.model = model.card
    }

    private fun setupCallbackListeners() {
        backButton.setOnClickListener(::dismiss)

        cancelButton.setOnClickListener(::dismiss)

        saveButton.setOnClickListener {
            viewModel.send(EditCardAction.Save)
            dismiss(saveButton)
        }

        saveAsDefaultTextView.setOnClickListener { viewModel.send(EditCardAction.ToggleDefaultCardState) }

        titleEditText.doOnTextChanged { text, start, count, after ->
            // TODO: temp hack
            if (count != after) {
                if (text!!.length <= CARD_TITLE_MAX_CHARACTERS) {
                    titleTextInputLayout.isErrorEnabled = false
                } else  {
                    titleTextInputLayout.isErrorEnabled = true
                    titleTextInputLayout.error =
                        getString(R.string.error_card_title_too_long, CARD_TITLE_MAX_CHARACTERS)
                }
                viewModel.send(EditCardAction.ChangeTitle(text.toString()))
            }
        }
    }

    private fun dismiss(view: View) {
        view.isEnabled = false
        findNavController().popBackStack()
    }

    private fun setupColorPicker() {
        val context = requireContext()
        val colorPickerAdapter = ColorPickerAdapter(emptyList()) {
            viewModel.send(EditCardAction.ChangePattern(it.pattern))
        }

        colorPickerAdapter.setHasStableIds(true)

        val manager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        colorPickerRecyclerView.apply {
            adapter = colorPickerAdapter
            layoutManager = manager
        }
    }
}

fun TokenizedCardEntity.toPaymentCardViewModel(newTitle: String, selectedPattern: CardPattern) =
    PaymentCardViewModel(
        id = id,
        cardNetwork = network,
        name = newTitle,
        maskedNumber = ending,
        expireDate = expireDate,
        pattern = selectedPattern
    )