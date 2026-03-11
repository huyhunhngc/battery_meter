package io.github.ifa.glancewidget.features.battery.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.DeviceType

@Composable
fun BonedDeviceItem(
    device: BonedDevice,
    modifier: Modifier = Modifier,
    showInWidget: Boolean = true,
    onShowInWidgetChanged: (String, Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            if (device.batteryInPercentage <= 0) {
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp),
                    painter = painterResource(device.deviceType.icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }
            Text(
                modifier = Modifier.widthIn(max = 150.dp),
                text = device.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (device.batteryInPercentage > 0) {
                    Icon(
                        imageVector = Icons.Rounded.Widgets,
                        contentDescription = "widget",
                        modifier = Modifier,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = showInWidget,
                    onCheckedChange = {
                        onShowInWidgetChanged(device.address, it)
                    },
                )
            }

        }
        if (device.batteryInPercentage > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                BatteryItem(
                    deviceType = device.deviceType,
                    percent = device.batteryInPercentage,
                    deviceName = device.name,
                    description = device.address,
                    isCharging = false,
                    isTransparent = true,
                    isShowLargeLevel = true,
                    modifier = Modifier.padding(8.dp)
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
            batteryInPercentage = 80,
            batteryInMinutes = 0,
            deviceType = DeviceType.OTHER
        ),
        onShowInWidgetChanged = { _, _ -> },
        showInWidget = false
    )
}