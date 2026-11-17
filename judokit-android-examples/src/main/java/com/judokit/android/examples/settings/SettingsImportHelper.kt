package com.judokit.android.examples.settings

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.gson.JsonParseException
import com.judokit.android.examples.R
import java.io.IOException

/**
 * Intent extra holding a settings JSON, base64 encoded, to import at launch. Lets UI tests set up the
 * app in one step, e.g. `adb shell am start -n <app>/.feature.DemoFeatureListActivity
 * --es import_settings_base64 <base64 JSON>`.
 *
 * `DemoFeatureListActivity` is `singleTop`: if it is already on top the extra arrives through
 * `onNewIntent`, otherwise a new instance imports it in `onCreate`. Any screen above it, such as an
 * in-progress payment, is left in place.
 */
const val IMPORT_SETTINGS_EXTRA = "import_settings_base64"

// UI tests read the import outcome from logcat under this tag.
private const val IMPORT_SETTINGS_LOG_TAG = "SettingsImport"

fun AppCompatActivity.showImportSettingsDialog(
    onFilePick: () -> Unit,
    onSuccess: () -> Unit = {},
) {
    val dialogView = layoutInflater.inflate(R.layout.dialog_import_settings, null)
    val editText = dialogView.findViewById<EditText>(R.id.jsonEditText)
    AlertDialog
        .Builder(this)
        .setTitle(R.string.import_settings_title)
        .setView(dialogView)
        .setPositiveButton(R.string.import_action) { _, _ ->
            val json = editText.text.toString().trim()
            if (json.isNotBlank()) applyImportSettings(json, onSuccess)
        }.setNeutralButton(R.string.import_from_file) { _, _ ->
            onFilePick()
        }.setNegativeButton(android.R.string.cancel, null)
        .show()
}

fun AppCompatActivity.applyImportSettings(
    json: String,
    onSuccess: () -> Unit = {},
) {
    try {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        SettingsImporter.import(prefs, json)
        onSuccess()
        Log.i(IMPORT_SETTINGS_LOG_TAG, "Settings imported")
        Toast.makeText(this, R.string.import_settings_success, Toast.LENGTH_SHORT).show()
    } catch (e: JsonParseException) {
        Log.w(IMPORT_SETTINGS_LOG_TAG, "Settings import failed: invalid JSON", e)
        Toast
            .makeText(this, getString(R.string.import_settings_invalid_json, e.localizedMessage), Toast.LENGTH_LONG)
            .show()
    } catch (e: IllegalArgumentException) {
        Log.w(IMPORT_SETTINGS_LOG_TAG, "Settings import failed", e)
        Toast
            .makeText(this, getString(R.string.import_settings_error, e.localizedMessage), Toast.LENGTH_LONG)
            .show()
    }
}

/**
 * Imports the settings JSON passed in [IMPORT_SETTINGS_EXTRA], if any. The extra is removed so a
 * recreated Activity doesn't import it again.
 */
fun AppCompatActivity.importSettingsFromIntent(intent: Intent?) {
    val encoded = intent?.getStringExtra(IMPORT_SETTINGS_EXTRA) ?: return
    intent.removeExtra(IMPORT_SETTINGS_EXTRA)
    val json =
        try {
            String(Base64.decode(encoded, Base64.DEFAULT), Charsets.UTF_8)
        } catch (e: IllegalArgumentException) {
            Log.w(IMPORT_SETTINGS_LOG_TAG, "Settings import failed: invalid base64", e)
            return
        }
    applyImportSettings(json)
}

fun AppCompatActivity.readImportedJson(
    uri: Uri?,
    onJson: (String) -> Unit,
) {
    uri ?: return
    try {
        val json =
            contentResolver
                .openInputStream(uri)
                ?.use { it.bufferedReader().readText() }
                ?: return
        onJson(json)
    } catch (e: IOException) {
        Toast
            .makeText(this, getString(R.string.import_read_file_error, e.localizedMessage), Toast.LENGTH_LONG)
            .show()
    } catch (e: SecurityException) {
        Toast
            .makeText(this, getString(R.string.import_read_file_error, e.localizedMessage), Toast.LENGTH_LONG)
            .show()
    }
}
