package io.github.ifa.glancewidget.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes

data class RuntimePermissionRequest(
    val permissions: List<String>,
    val required: Boolean = false,
    @StringRes val rationale: Int? = null,
    val onResult: (isSuccess: Boolean) -> Unit,
) {
    init {
        if (required) {
            assert(rationale != null) { "must set rationale if required" }
        }
    }
}

fun Context.isPermissionGranted(permission: String): Boolean =
    checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

fun Context.checkPermissions(permissions: List<String>): Boolean =
    permissions.all { isPermissionGranted(it) }

fun Context.isNotificationPermissionGranted(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
} else {
    true
}