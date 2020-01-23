package com.judopay.util

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager

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