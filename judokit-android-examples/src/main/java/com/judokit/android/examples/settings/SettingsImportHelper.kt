package com.judokit.android.examples.settings

import android.net.Uri
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.gson.JsonParseException
import com.judokit.android.examples.R

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
        Toast.makeText(this, R.string.import_settings_success, Toast.LENGTH_SHORT).show()
    } catch (e: JsonParseException) {
        Toast
            .makeText(this, getString(R.string.import_settings_invalid_json, e.localizedMessage), Toast.LENGTH_LONG)
            .show()
    } catch (e: Exception) {
        Toast
            .makeText(this, getString(R.string.import_settings_error, e.localizedMessage), Toast.LENGTH_LONG)
            .show()
    }
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
    } catch (e: Exception) {
        Toast
            .makeText(this, getString(R.string.import_read_file_error, e.localizedMessage), Toast.LENGTH_LONG)
            .show()
    }
}
