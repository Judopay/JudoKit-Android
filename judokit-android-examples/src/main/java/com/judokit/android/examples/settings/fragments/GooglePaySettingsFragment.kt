package com.judokit.android.examples.settings.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import com.judokit.android.examples.R

class GooglePaySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        setPreferencesFromResource(R.xml.google_pay_preferences, rootKey)
        setupGooglePayMitTypeVisibility()
    }

    private fun setupGooglePayMitTypeVisibility() {
        val mitTypePreference = findPreference<Preference>("google_pay_mit_type") ?: return
        updateGooglePayMitTypeVisibility(mitTypePreference.sharedPreferences?.getString("google_pay_mit_type", "NONE"))
        mitTypePreference.setOnPreferenceChangeListener { _, newValue ->
            updateGooglePayMitTypeVisibility(newValue as? String)
            true
        }
    }

    private fun updateGooglePayMitTypeVisibility(mitType: String?) {
        val type = mitType ?: "NONE"
        val isMitEnabled = type != "NONE"

        listOf(
            "google_pay_mit_management_url",
            "google_pay_mit_billing_agreement",
            "google_pay_mit_immediate_total_price",
            "google_pay_mit_immediate_display_items",
        ).forEach { key ->
            findPreference<Preference>(key)?.isVisible = isMitEnabled
        }

        findPreference<PreferenceCategory>("google_pay_deferred_category")?.isVisible =
            type == "DEFERRED"
        findPreference<PreferenceCategory>("google_pay_recurring_category")?.isVisible =
            type == "RECURRING"
    }
}
