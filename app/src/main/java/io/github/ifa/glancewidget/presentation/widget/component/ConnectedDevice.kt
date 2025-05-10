package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.ui.component.SessionText

@Composable
fun ConnectedDevice(
    modifier: Modifier = Modifier,
    batteryConnectedDevice: List<BonedDevice>,
    onShowInWidgetChanged: (BonedDevice, Boolean) -> Unit,
    onItemClick: (BonedDevice) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SessionText(
            text = stringResource(id = R.string.connected_devices),
        )

        batteryConnectedDevice.forEach {
            BonedDeviceItem(
                device = it,
                showInWidget = true,
                onShowInWidgetChanged = onShowInWidgetChanged,
                onItemClick = onItemClick
            )
        }
    }
}