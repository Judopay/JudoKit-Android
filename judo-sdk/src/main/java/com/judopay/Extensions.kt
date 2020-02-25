package com.judopay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.judopay.model.ApiEnvironment
import com.judopay.ui.error.JudoConfigObjectNotProvidedError

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
    return if (parent != null && parent::class.java == parentType) {
        return parent as T
    } else (parent as? View)?.parentOfType(parentType)
}

fun ViewGroup.inflate(resource: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(resource, this, attachToRoot)
}

val FragmentActivity.judo: Judo
    get() = intent.getParcelableExtra(JUDO_OPTIONS)
            ?: throw JudoConfigObjectNotProvidedError()

val Fragment.judo: Judo
    get() = requireActivity().judo