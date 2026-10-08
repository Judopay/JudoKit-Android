package com.judopay.judokit.android.service

import android.content.Context

/**
 * Public entry point for managing the on-device cache of 3-D Secure 2 directory-server (DS)
 * certificates that the SDK fetches from the Judo CDN.
 *
 * The SDK keeps these certificates in a small `SharedPreferences` store and refreshes them
 * automatically in the background at payment-screen creation. Clearing the cache is normally
 * unnecessary and is provided mainly for QA and support scenarios that require a forced
 * re-fetch on the next transaction.
 */
object DsCertificateCache {
    /**
     * Removes every locally cached DS certificate.
     *
     * After clearing, the next card transaction falls back to the 3DS SDK's built-in
     * certificates until the background refresh re-populates the cache from the CDN. Safe to
     * call from any thread.
     *
     * @param context any [Context]; the application context is used internally.
     */
    @JvmStatic
    fun clear(context: Context) {
        DsCertsCacheStore(context.applicationContext).clear()
    }
}
