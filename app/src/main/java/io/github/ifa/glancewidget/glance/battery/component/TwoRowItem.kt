package io.github.ifa.glancewidget.glance.battery.component

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import io.github.ifa.glancewidget.glance.battery.BatteryWidget.Companion.PADDING
import io.github.ifa.glancewidget.model.BonedDevice

@Composable
fun TwoRowItem(
    connectedDevices: List<BonedDevice>,
    modifier: GlanceModifier,
) {
    connectedDevices.forEach { device ->
        Spacer(modifier = GlanceModifier.height(PADDING))
        BatteryItem(
            deviceType = device.deviceType,
            percent = device.batteryInPercentage,
            isCharging = false,
            deviceName = device.name.toString(),
            modifier = modifier
        )
    }
}