package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.DeviceType

@Composable
fun BonedDeviceItem(
    device: BonedDevice,
    modifier: Modifier = Modifier,
    showInWidget: Boolean = true,
    onShowInWidgetChanged: (BonedDevice, Boolean) -> Unit,
    onItemClick: (BonedDevice) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onItemClick(device) },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = showInWidget,
                    onCheckedChange = {
                        onShowInWidgetChanged(device, it)
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Show in widget",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                BatteryItem(
                    deviceType = device.deviceType,
                    percent = device.batteryInPercentage.coerceAtLeast(0),
                    deviceName = device.name,
                    description = device.address,
                    isCharging = false,
                    isTransparent = true,
                    isShowLargeLevel = false
                )
            }
        }
    }
}

@Preview
@Composable
fun BonedDeviceItemPreview() {
    BonedDeviceItem(
        device = BonedDevice(
            name = "MX keys board MX keys board",
            address = "AF:BB:99:00:00:00",
            batteryInPercentage = 0,
            batteryInMinutes = 0,
            deviceType = DeviceType.OTHER
        ),
        onItemClick = {},
        onShowInWidgetChanged = { _, _ -> },
        showInWidget = false
    )
}