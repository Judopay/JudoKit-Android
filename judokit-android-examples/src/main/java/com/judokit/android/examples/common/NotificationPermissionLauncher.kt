package com.judokit.android.examples.common

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.ref.WeakReference

const val NOTIFICATION_PERMISSION_IS_REQUIRED_TITLE = "Permissions required"
const val NOTIFICATION_PERMISSION_IS_REQUIRED_MESSAGE = "Notification permission is required, allow notification permission for this app."
const val BUTTON_OK = "OK"
const val BUTTON_CANCEL = "Cancel"

class NotificationPermissionLauncher(activity: AppCompatActivity) {
    private var activityReference: WeakReference<AppCompatActivity> = WeakReference(activity)
    var hasNotificationPermissionGranted = false

    private val permissionLauncher =
        activityReference.get()!!.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted

            if (isGranted) {
                return@registerForActivityResult
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (activity.shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                    showPermissionRationaleDialog()
                } else {
                    showNavigateToSettingsDialog()
                }
            }
        }

    fun requestPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }
    }

    private fun showNavigateToSettingsDialog() {
        activityReference.get()?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(NOTIFICATION_PERMISSION_IS_REQUIRED_TITLE)
                .setMessage(NOTIFICATION_PERMISSION_IS_REQUIRED_MESSAGE)
                .setPositiveButton(BUTTON_OK) { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:${it.applicationContext.packageName}")
                    it.startActivity(intent)
                }
                .setNegativeButton(BUTTON_CANCEL, null)
                .show()
        }
    }

    private fun showPermissionRationaleDialog() {
        activityReference.get()?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(NOTIFICATION_PERMISSION_IS_REQUIRED_TITLE)
                .setMessage(NOTIFICATION_PERMISSION_IS_REQUIRED_MESSAGE)
                .setPositiveButton(BUTTON_OK) { _, _ ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                .setNegativeButton(BUTTON_CANCEL, null)
                .show()
        }
    }
}
