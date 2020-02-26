package com.judopay

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textfield.TextInputLayout
import com.judopay.model.ApiEnvironment
import com.judopay.ui.error.JudoNotProvidedError

internal val Judo.apiBaseUrl: String
    get() = if (isSandboxed) ApiEnvironment.SANDBOX.host else ApiEnvironment.LIVE.host

internal fun requireNotNullOrEmpty(value: String?, propertyName: String): String {
    if (value.isNullOrEmpty())
        throw IllegalArgumentException("$propertyName cannot be null or empty")
    else return value
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