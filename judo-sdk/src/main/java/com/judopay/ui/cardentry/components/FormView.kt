package com.judopay.ui.cardentry.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.judopay.parentOfType
import com.judopay.subViewsWithType
import com.judopay.ui.cardentry.model.*

data class FormViewModel(
        val formModel: FormModel,
        val enabledFields: List<FormFieldType>,
        val fieldMappings: Map<Int, FieldConfiguration>
)

class FormView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    interface OnSubmitListener {
        fun onSubmitForm(form: FormView, model: FormModel)
    }

    var model = FormViewModel(FormModel(), emptyList(), emptyMap())
        set(value) {
            field = value
            update()
        }

    private val formFieldMapping = mutableMapOf<FormFieldType, TextInputEditText>()
    private val inputMasks = mutableMapOf<FormFieldType, InputMaskTextWatcher>()
    private var submitButton: Button? = null

    internal var onSubmitListener: OnSubmitListener? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupFields()
    }

    private fun setupFields() {
        val inputEditText = subViewsWithType(TextInputEditText::class.java)
        val buttons = subViewsWithType(MaterialButton::class.java)

        inputEditText.forEach { editText ->
            // configure the filed only if we know about it
            model.fieldMappings[editText.id]?.let { config ->
                configureEditText(editText, config as InputFieldConfiguration)
            }
        }

        buttons.forEach { button ->
            model.fieldMappings[button.id]?.let { config ->
                configureSubmitButton(button, config as SubmitFieldConfiguration)
            }
        }
    }

    private fun update() {
        formFieldMapping.clear()
        setupFields()
    }

    private fun configureSubmitButton(button: Button, config: SubmitFieldConfiguration) {
        submitButton = button
        button.text = config.text
        button.setOnClickListener {
            onSubmitListener?.onSubmitForm(this, FormModel(
                    getFormValueForFieldType(FormFieldType.NUMBER) ?: "",
                    getFormValueForFieldType(FormFieldType.HOLDER_NAME) ?: "",
                    getFormValueForFieldType(FormFieldType.EXPIRATION_DATE) ?: "",
                    getFormValueForFieldType(FormFieldType.SECURITY_NUMBER) ?: "",
                    getFormValueForFieldType(FormFieldType.COUNTRY) ?: "",
                    getFormValueForFieldType(FormFieldType.POST_CODE) ?: ""
            ))
        }
    }

    private fun configureEditText(editText: TextInputEditText, config: InputFieldConfiguration) {
        // keep a reference to the editText for later to extract the input data
        formFieldMapping[config.type] = editText

        // if the field is disabled, hide it from the layout
        val visibility = if (model.enabledFields.contains(config.type)) View.VISIBLE else View.GONE
        editText.parentOfType(TextInputLayout::class.java)?.visibility = visibility

        // setup field properties
        editText.setText(model.formModel.getValueForFieldType(config.type))

        val mask = when (config.type) {
            FormFieldType.NUMBER -> CardNumberInputMaskTextWatcher(editText)
            FormFieldType.EXPIRATION_DATE -> InputMaskTextWatcher(editText, "##/##")
            FormFieldType.SECURITY_NUMBER -> InputMaskTextWatcher(editText, "###")
            else -> null
        }

        inputMasks[config.type]?.let {
            editText.removeTextChangedListener(it)
        }
        editText.setHint(config.hint)
        mask?.let {
            editText.addTextChangedListener(it)
            inputMasks[config.type] = it
        }
    }


    private fun getFormValueForFieldType(type: FormFieldType): String? {
        return formFieldMapping[type]?.text.toString()
    }

}