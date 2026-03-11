package io.github.ifa.glancewidget.features.settings.component

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BluetoothConnected
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.github.ifa.glancewidget.BuildConfig
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.ui.component.AppAlertDialog
import io.github.ifa.glancewidget.ui.component.SwitchWithDescription
import io.github.ifa.glancewidget.utils.BluetoothPermissions
import io.github.ifa.glancewidget.utils.checkPermissions
import io.github.ifa.glancewidget.utils.isNotificationPermissionGranted

val NotificationSettingsIntent by lazy {
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
    }
}

val ApplicationDetailsSettingsIntent by lazy {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WidgetSettings(
    notificationSetting: AppSettings.NotificationSetting,
    syncColorEnabled: Boolean,
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
    var syncEnabled by remember(syncColorEnabled) {
        mutableStateOf(syncColorEnabled)
    }

    LifecycleResumeEffect(notificationSetting) {
        showPairedDevice = showPairedDevice && context.checkPermissions(BluetoothPermissions)
        notificationEnabled = notificationEnabled && context.isNotificationPermissionGranted()
        onPauseOrDispose {}
    }

    val bluetoothPermissions =
        rememberMultiplePermissionsState(BluetoothPermissions) { permissions ->
            val isGranted = permissions.entries.all { it.value }
            if (isGranted) {
                onSetShowPairedDevice(true)
            }
        }

    val notificationPermission = rememberNotificationPermissionState { isGranted ->
        if (isGranted) {
            onSetNotificationEnabled(true)
        }
    }

    var dialogUiState by remember { mutableStateOf(SettingAlertDialogUiState()) }
    if (dialogUiState.isOpen) {
        AppAlertDialog(
            onDismissRequest = {
            dialogUiState = dialogUiState.close
            dialogUiState.onDismissRequest()
        },
            onConfirmation = {
                dialogUiState = dialogUiState.close
                dialogUiState.onConfirmation()
            },
            dialogTitle = stringResource(id = dialogUiState.title),
            dialogText = stringResource(
                id = dialogUiState.text, stringResource(id = R.string.app_name)
            ),
            positiveButtonText = stringResource(id = dialogUiState.positiveButtonText),
            negativeButtonText = "",
            icon = dialogUiState.iconRes?.let { ImageVector.vectorResource(id = it) }
                ?: dialogUiState.icon)
    }

    SwitchWithDescription(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp)),
        label = stringResource(id = R.string.battery_alert),
        description = stringResource(id = R.string.battery_alert_desc),
        icon = Icons.Rounded.BatteryChargingFull,
        onCheckedChange = scope@{ checked ->
            if (notificationPermission == null) {
                onSetNotificationEnabled(checked)
                notificationEnabled = checked
                return@scope
            }
            notificationEnabled = checked
            if (!notificationPermission.status.isGranted && checked) {
                if (notificationPermission.status.shouldShowRationale) {
                    notificationPermission.launchPermissionRequest()
                } else {
                    dialogUiState = SettingAlertDialogUiState(
                        isOpen = true,
                        title = R.string.need_to_allow_notification,
                        text = R.string.allow_notification,
                        onDismissRequest = { notificationEnabled = false },
                        positiveButtonText = R.string.go_to_app_settings,
                        icon = Icons.Default.Notifications,
                        onConfirmation = {
                            onSetNotificationEnabled(true)
                            context.startActivity(NotificationSettingsIntent, null)
                        })
                }
            } else {
                onSetNotificationEnabled(checked)
            }
        },
        checked = notificationEnabled,
    )

    Spacer(modifier = Modifier.height(16.dp))

    SwitchWithDescription(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp)),
        label = stringResource(id = R.string.show_paired_devices),
        description = stringResource(id = R.string.show_paired_devices_desc),
        icon = Icons.Rounded.BluetoothConnected,
        onCheckedChange = checkedChange@{ checked ->
            showPairedDevice = checked
            if (!context.checkPermissions(BluetoothPermissions) && checked) {
                if (bluetoothPermissions.shouldShowRationale) {
                    bluetoothPermissions.launchMultiplePermissionRequest()
                } else {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        onSetNotificationEnabled(true)
                        return@checkedChange
                    }
                    dialogUiState = SettingAlertDialogUiState(
                        isOpen = true,
                        title = R.string.need_to_grant_permission,
                        text = R.string.grant_permission_bluetooth_guide,
                        onDismissRequest = { showPairedDevice = false },
                        iconRes = R.drawable.ic_nearby_off,
                        onConfirmation = {
                            onSetShowPairedDevice(true)
                            context.startActivity(ApplicationDetailsSettingsIntent, null)
                        })
                }
            } else {
                onSetShowPairedDevice(checked)
            }
        },
        checked = showPairedDevice,
    )

    SwitchWithDescription(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
        icon = Icons.Rounded.Sync,
        label = stringResource(id = R.string.sync_color_scheme_with_widget),
        description = stringResource(id = R.string.sync_color_scheme_with_widget_description),
        onCheckedChange = scope@{ checked ->
            syncEnabled = checked
        },
        checked = syncEnabled,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberNotificationPermissionState(
    onPermissionResult: (Boolean) -> Unit = {}
): PermissionState? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS,
            onPermissionResult = onPermissionResult
        )
    } else null
}

data class SettingAlertDialogUiState(
    val isOpen: Boolean = false,
    @param:StringRes val title: Int = R.string.need_to_grant_permission,
    @param:StringRes val text: Int = R.string.grant_permission_bluetooth_guide,
    @param:StringRes val positiveButtonText: Int = R.string.go_to_app_info,
    val icon: ImageVector = Icons.Rounded.Warning,
    @param:DrawableRes val iconRes: Int? = null,
    val onDismissRequest: () -> Unit = {},
    val onConfirmation: () -> Unit = {},
) {
    val close: SettingAlertDialogUiState get() = this.copy(isOpen = false)
}