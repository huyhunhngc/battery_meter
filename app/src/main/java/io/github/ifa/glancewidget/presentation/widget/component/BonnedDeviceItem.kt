package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.ui.theme.containerColorAlpha60

@Composable
fun BonedDeviceItem(
    device: BonedDevice,
    modifier: Modifier = Modifier,
    showInWidget: Boolean = true,
    onShowInWidgetChanged: (String, Boolean) -> Unit,
) {
        Column(
            modifier = modifier.padding(vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(containerColorAlpha60)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.widthIn(max = 150.dp),
                    text = device.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_widgets),
                        contentDescription = "widget",
                        modifier = Modifier,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = showInWidget,
                        onCheckedChange = {
                            onShowInWidgetChanged(device.address, it)
                        },
                    )
                }

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
                    isShowLargeLevel = true,
                    modifier = Modifier.padding(8.dp)
                )
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