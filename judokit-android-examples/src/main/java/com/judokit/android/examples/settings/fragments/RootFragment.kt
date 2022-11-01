package com.judokit.android.examples.settings.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.judokit.android.examples.R

class RootFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
