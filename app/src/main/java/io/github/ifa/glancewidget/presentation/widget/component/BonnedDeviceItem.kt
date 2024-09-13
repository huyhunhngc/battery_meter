package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.DeviceType

@Composable
fun BonedDeviceItem(
    device: BonedDevice, modifier: Modifier = Modifier, onItemClick: (BonedDevice) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BatteryItem(
            deviceType = device.deviceType,
            percent = device.batteryInPercentage,
            deviceName = device.name,
            isCharging = false,
            isTransparent = true,
            isShowLargeLevel = true,
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(0.5f)
                .clickable { onItemClick(device) }
        )
        Column(
            modifier = Modifier
                .height(100.dp)
                .weight(1f)
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
            ,
            verticalArrangement = Arrangement.Center
        ) {
            ShortInformationRow(value = device.name)
            ShortInformationRow(value = device.address)
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
        onItemClick = {}
    )
}