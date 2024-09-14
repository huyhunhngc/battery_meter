package io.github.ifa.glancewidget.presentation.settings.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.ui.component.SwitchWithDescription
import io.github.ifa.glancewidget.ui.component.TextWithImage
import io.github.ifa.glancewidget.utils.BluetoothPermissions
import io.github.ifa.glancewidget.utils.checkPermissions

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
        onPauseOrDispose {}
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
            onSetShowPairedDevice(checked)
            showPairedDevice = checked
        },
        checked = showPairedDevice
    )
}
