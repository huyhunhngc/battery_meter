package io.github.ifa.glancewidget.glance.battery.component

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import io.github.ifa.glancewidget.glance.battery.BatteryWidget.Companion.PADDING
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.WidgetSetting

@Composable
fun GridWrapItem(
    connectedDevice: List<BonedDevice>, modifier: GlanceModifier
) {
    connectedDevice.chunked(2).forEach { devices ->
        Spacer(modifier = GlanceModifier.height(PADDING))
        Row(modifier = modifier.fillMaxWidth()) {
            devices.forEachIndexed { index, device ->
                if (index == 1) Spacer(modifier = GlanceModifier.width(PADDING))
                val deviceNameWidth = if (devices.size == 2) 50 else 120
                BatteryItem(
                    deviceType = device.deviceType,
                    percent = device.batteryInPercentage,
                    isCharging = false,
                    deviceName = device.name.toString(),
                    deviceNameWidth = deviceNameWidth,
                    modifier = GlanceModifier.defaultWeight()
                )
            }
        }
    }
}