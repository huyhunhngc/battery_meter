package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.model.BonedDevice

@Composable
fun BonedDeviceItem(
    device: BonedDevice, modifier: Modifier = Modifier, onItemClick: (BonedDevice) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth()) {

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
        )
    }
}