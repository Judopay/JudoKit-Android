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
        val isStandardTransaction = type == "NONE"

        listOf(
            "google_pay_transaction_id",
            "google_pay_total_price_status",
            "google_pay_total_price_label",
            "google_pay_checkout_option",
        ).forEach { key ->
            findPreference<Preference>(key)?.isVisible = isStandardTransaction
        }

        findPreference<PreferenceCategory>("google_pay_mit_shared_category")?.isVisible =
            !isStandardTransaction
        findPreference<PreferenceCategory>("google_pay_deferred_category")?.isVisible =
            type == "DEFERRED"
        findPreference<PreferenceCategory>("google_pay_recurring_category")?.isVisible =
            type == "RECURRING"
    }
}
