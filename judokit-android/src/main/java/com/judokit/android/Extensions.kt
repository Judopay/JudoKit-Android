package com.judokit.android

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.judokit.android.model.Amount
import com.judokit.android.model.ApiEnvironment
import com.judokit.android.model.Currency
import com.judokit.android.model.PaymentWidgetType
import com.judokit.android.ui.common.ANIMATION_DURATION_500
import com.judokit.android.ui.error.JudoNotProvidedError

internal val Judo.apiBaseUrl: String
    get() = if (isSandboxed) ApiEnvironment.SANDBOX.host else ApiEnvironment.LIVE.host

internal fun requireNotNullOrEmpty(value: String?, propertyName: String): String {
    when {
        value == null -> throw IllegalArgumentException("$propertyName cannot be null")
        value.isEmpty() -> throw IllegalArgumentException("$propertyName cannot be empty")
        else -> return value
    }
}

internal fun <T : Any> requireNotNull(value: T?, propertyName: String): T {
    if (value == null)
        throw IllegalArgumentException("$propertyName cannot be null")
    else return value
}

@Suppress("UNCHECKED_CAST")
fun <T : View> View.parentOfType(parentType: Class<T>): T? {
    var parent = parent
    while (parent is View) {
        if (parent::class.java == parentType) {
            return parent as T
        }
        parent = parent.getParent()
    }
    return null
}

@Suppress("UNCHECKED_CAST")
fun <T : View> ViewGroup.subViewsWithType(subviewType: Class<T>): List<T> {
    val views = mutableListOf<T>()
    children.forEach {
        if (it::class.java == subviewType) {
            views.add(it as T)
        } else if (it is ViewGroup) {
            views.addAll(it.subViewsWithType(subviewType))
        }
    }
    return views
}

fun ViewGroup.inflate(resource: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(resource, this, attachToRoot)
}

val FragmentActivity.judo: Judo
    get() = intent.getParcelableExtra(JUDO_OPTIONS)
        ?: throw JudoNotProvidedError()

val Fragment.judo: Judo
    get() = requireActivity().judo

val String.withWhitespacesRemoved: String
    get() = replace("\\s".toRegex(), "")

fun Bundle.toMap(): Map<String, String> = keySet().mapNotNull {
    val value = getString(it)
    if (value != null) Pair(it, value) else null
}.toMap()

fun View.animateWithAlpha(alpha: Float, duration: Long = ANIMATION_DURATION_500) {
    animate()
        .alpha(alpha)
        .duration = duration
}

fun View.animateWithTranslation(
    translationY: Float,
    alpha: Float,
    duration: Long = ANIMATION_DURATION_500
) {
    animate().translationY(translationY)
        .alpha(alpha)
        .duration = duration
}

fun View.dismissKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Any.toJSONString(): String = Gson().toJson(this)

internal fun requireAmount(paymentWidgetType: PaymentWidgetType, amount: Amount?): Amount {
    val defaultAmount = Amount.Builder().setAmount("").setCurrency(Currency.GBP)
    return if (arrayOf(
            PaymentWidgetType.CHECK_CARD,
            PaymentWidgetType.CREATE_CARD_TOKEN,
            PaymentWidgetType.REGISTER_CARD
        ).contains(paymentWidgetType)
    ) {
        defaultAmount.build()
    } else {
        requireNotNullOrEmpty(amount?.amount, "amount")
        amount!!
    }
}

internal fun Window.configureDialog() {
    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    requestFeature(Window.FEATURE_NO_TITLE)
    setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    )
    setDimAmount(0.0f)
}
