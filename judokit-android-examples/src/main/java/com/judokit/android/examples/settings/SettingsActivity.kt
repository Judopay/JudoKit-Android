package com.judokit.android.examples.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.judokit.android.examples.R
import com.judokit.android.examples.settings.fragments.RootFragment
import com.judokit.android.examples.settings.fragments.ThreeDSSDKUICustomisationFragment

class SettingsActivity :
    AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            readImportedJson(uri) { applyImportSettings(it, ::reloadRootFragment) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_activity)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, RootFragment())
            .commit()

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                    true
                } else {
                    super.onOptionsItemSelected(item)
                }
            }
            R.id.action_import_settings -> {
                showImportSettingsDialog(
                    onFilePick = { pickFileLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
                    onSuccess = ::reloadRootFragment,
                )
                true
            }
            R.id.action_export_settings -> {
                exportSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference,
    ): Boolean {
        val args = pref.extras
        val fragment = ThreeDSSDKUICustomisationFragment()
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()

        return true
    }

    private fun reloadRootFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, RootFragment())
            .commit()
    }

    private fun exportSettings() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val json = SettingsImporter.export(prefs)
        val clipboard = getSystemService(ClipboardManager::class.java)
        clipboard.setPrimaryClip(ClipData.newPlainText("judo_settings", json))
        Toast.makeText(this, R.string.export_settings_copied, Toast.LENGTH_SHORT).show()
    }
}
