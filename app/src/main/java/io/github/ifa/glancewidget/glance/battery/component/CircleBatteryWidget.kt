package io.github.ifa.glancewidget.glance.battery.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.unit.ColorProvider
import io.github.ifa.glancewidget.glance.battery.BatteryWidget.Companion.PADDING
import io.github.ifa.glancewidget.glance.battery.utils.cornerRadiusCompat
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.model.WidgetSetting

@Composable
fun CircleBatteryWidget(
    battery: BatteryData?,
    percent: Int,
    isCharging: Boolean,
    isTransparent: Boolean,
    connectedDevice: List<BonedDevice>,
    sizeWidget: WidgetSetting.Type,
) {
    val transparent by rememberUpdatedState(newValue = isTransparent)
    val deviceType by remember(battery) {
        derivedStateOf { battery?.myDevice?.deviceType ?: DeviceType.PHONE }
    }
    val deviceName by remember(battery) {
        derivedStateOf { battery?.myDevice?.name.toString() }
    }
    val charging by rememberUpdatedState(newValue = isCharging)
    val connectedDevices by remember(connectedDevice, sizeWidget) {
        derivedStateOf { connectedDevice.take(sizeWidget.itemOnSizeForCircle()) }
    }
    val scaleTextSize = remember(sizeWidget) {
        if (sizeWidget == WidgetSetting.Type.Large || sizeWidget == WidgetSetting.Type.Wide) {
            1.5f
        } else {
            1.0f
        }
    }

    Box(
        modifier = GlanceModifier.fillMaxSize()
            .padding(PADDING)
            .cornerRadiusCompat(
                24,
                if (transparent) {
                    ColorProvider(Color.Transparent)
                } else {
                    GlanceTheme.colors.widgetBackground
                }
            ),
    ) {
        Row(modifier = GlanceModifier.fillMaxSize()) {
            CircleBatteryItem(
                deviceType = deviceType,
                percent = percent,
                isCharging = charging,
                deviceName = deviceName,
                scaleTextSize = scaleTextSize,
                modifier = GlanceModifier.defaultWeight()
            )
            connectedDevices.forEach {
                CircleBatteryItem(
                    deviceType = it.deviceType,
                    percent = it.batteryInPercentage,
                    isCharging = false,
                    deviceName = it.name,
                    scaleTextSize = scaleTextSize,
                    modifier = GlanceModifier.defaultWeight()
                )
            }
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 200, heightDp = 400)
@Composable
fun CircleBatteryWidgetPreview() {
    CircleBatteryWidget(
        battery = null,
        percent = 70,
        isCharging = true,
        isTransparent = false,
        connectedDevice = listOf(BonedDevice(batteryInPercentage = 39), BonedDevice()),
        sizeWidget = WidgetSetting.Type.Default
    )
}