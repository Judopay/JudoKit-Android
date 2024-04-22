package com.judopay.judokit.android.ui.paymentmethods.model

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T? = null) {
    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Checks if the event has been handled and prevents its use again.
     */
    fun hasBeenHandled(): Boolean {
        return if (hasBeenHandled) {
            true
        } else {
            hasBeenHandled = true
            false
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T? = content
}
