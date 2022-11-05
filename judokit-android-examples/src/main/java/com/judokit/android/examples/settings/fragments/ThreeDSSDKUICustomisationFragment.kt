package com.judokit.android.examples.settings.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.judokit.android.examples.R

class ThreeDSSDKUICustomisationFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.three_ds_sdk_ui_customisation_preferences, rootKey)
    }
}
