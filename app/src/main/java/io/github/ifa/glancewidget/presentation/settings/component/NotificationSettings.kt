package io.github.ifa.glancewidget.presentation.settings.component

import android.Manifest
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.presentation.settings.ApplicationDetailsSettingsIntent
import io.github.ifa.glancewidget.ui.component.AppAlertDialog
import io.github.ifa.glancewidget.ui.component.SwitchWithDescription
import io.github.ifa.glancewidget.ui.component.TextWithImage
import io.github.ifa.glancewidget.utils.BluetoothPermissions
import io.github.ifa.glancewidget.utils.checkPermissions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationSetting(
    notificationSetting: AppSettings.NotificationSetting,
    onSetNotificationEnabled: (Boolean) -> Unit,
    onSetShowPairedDevice: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    var notificationEnabled by remember(notificationSetting.batteryAlert) {
        mutableStateOf(notificationSetting.batteryAlert)
    }
    var showPairedDevice by remember(notificationSetting.showPairedDevices) {
        mutableStateOf(notificationSetting.showPairedDevices)
    }
    LifecycleResumeEffect(Unit) {
        showPairedDevice = showPairedDevice && context.checkPermissions(BluetoothPermissions)
        notificationEnabled = notificationEnabled && context.checkPermissions(listOf(Manifest.permission.POST_NOTIFICATIONS))
        onPauseOrDispose {}
    }

    val bluetoothPermissions = rememberMultiplePermissionsState(BluetoothPermissions) { permissions ->
        val isGranted = permissions.entries.all { it.value }
        if (isGranted) {
            onSetShowPairedDevice(true)
        }
    }

    val notificationPermission = rememberPermissionState(
        permission = Manifest.permission.POST_NOTIFICATIONS
    ) { isGranted ->
        onSetNotificationEnabled(isGranted)
    }

    var openDialogUiState by remember { mutableStateOf(OpenDialogUiState()) }
    if (openDialogUiState.isOpen) {
        AppAlertDialog(
            onDismissRequest = {
                openDialogUiState = openDialogUiState.close()
                openDialogUiState.onDismissRequest()
            },
            onConfirmation = {
                openDialogUiState = openDialogUiState.close()
                context.startActivity(ApplicationDetailsSettingsIntent, null)
                openDialogUiState.onConfirmation()
            },
            dialogTitle = stringResource(id = R.string.need_to_grant_permission),
            dialogText = stringResource(id = R.string.grant_permission_bluetooth_guide),
            positiveButtonText = stringResource(id = R.string.go_to_app_settings),
            negativeButtonText = "",
            icon = Icons.Default.Warning
        )
    }

    TextWithImage(
        text = stringResource(R.string.notification_settings),
        image = painterResource(id = R.drawable.ic_notifications),
        modifier = Modifier.padding(vertical = 16.dp)
    )

    SwitchWithDescription(
        label = stringResource(id = R.string.battery_alert),
        description = stringResource(id = R.string.battery_alert_desc),
        onCheckedChange = { checked ->
            if (!notificationPermission.status.isGranted && checked) {
                if (notificationPermission.status.shouldShowRationale) {
                    notificationPermission.launchPermissionRequest()
                } else {
                    openDialogUiState = OpenDialogUiState(
                        isOpen = true,
                        title = R.string.need_to_grant_permission,
                        text = R.string.grant_permission_bluetooth_guide,
                        onDismissRequest = { notificationEnabled = false }
                    )
                }
            } else {
                onSetNotificationEnabled(checked)
            }
            onSetNotificationEnabled(checked)
            notificationEnabled = checked
        },
        checked = notificationEnabled
    )

    Spacer(modifier = Modifier.height(16.dp))

    SwitchWithDescription(
        label = stringResource(id = R.string.show_paired_devices),
        description = stringResource(id = R.string.show_paired_devices_desc),
        onCheckedChange = { checked ->
            if (!context.checkPermissions(BluetoothPermissions) && checked) {
                onSetShowPairedDevice(false)
                if (bluetoothPermissions.shouldShowRationale) {
                    bluetoothPermissions.launchMultiplePermissionRequest()
                } else {
                    openDialogUiState = OpenDialogUiState(
                        isOpen = true,
                        title = R.string.need_to_grant_permission,
                        text = R.string.grant_permission_bluetooth_guide,
                        onDismissRequest = { showPairedDevice = false }
                    )
                }
            } else {
                onSetShowPairedDevice(checked)
            }
            showPairedDevice = checked
        },
        checked = showPairedDevice
    )
}

data class OpenDialogUiState(
    val isOpen: Boolean = false,
    @StringRes val title: Int = R.string.need_to_grant_permission,
    @StringRes val text: Int = R.string.grant_permission_bluetooth_guide,
    val onDismissRequest: () -> Unit = {},
    val onConfirmation: () -> Unit = {},
) {
    fun close() = this.copy(isOpen = false)
}