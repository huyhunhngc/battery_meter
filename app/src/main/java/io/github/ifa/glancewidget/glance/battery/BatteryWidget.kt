package io.github.ifa.glancewidget.glance.battery

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.glance.battery.component.BatteryItem
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.model.WidgetSize
import io.github.ifa.glancewidget.utils.fromJson
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.flow.first

class BatteryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batteryWidgetStore = context.batteryWidgetStore
        val initial = batteryWidgetStore.data.first()

        provideContent {
            val data by batteryWidgetStore.data.collectAsState(initial)
            Content(
                battery = fromJson<BatteryData>(data[BATTERY_PREFERENCES]),
                size = fromJson<WidgetSize>(data[WIDGET_PREFERENCES]) ?: WidgetSize(100, 90)
            )
        }

    }

    suspend fun updateIfBatteryChanged(
        context: Context, glanceId: GlanceId, batteryData: BatteryData?
    ) {
        context.batteryWidgetStore.setObject(BATTERY_PREFERENCES, batteryData)
        update(context, glanceId)
    }

    suspend fun updateOnSizeChanged(
        context: Context, glanceId: GlanceId, widgetSize: WidgetSize
    ) {
        context.batteryWidgetStore.setObject(WIDGET_PREFERENCES, widgetSize)
        update(context, glanceId)
    }

    @Composable
    private fun Content(
        battery: BatteryData?,
        size: WidgetSize = WidgetSize(100, 90)
    ) {
        val percent = battery?.myDevice?.level ?: 100
        val isCharging = battery?.myDevice?.isCharging ?: false
        val connectedDevice = battery?.batteryConnectedDevices?.take(2)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(PADDING)
                .background(GlanceTheme.colors.primaryContainer),
        ) {
            Column(modifier = GlanceModifier.fillMaxSize()) {
                BatteryItem(
                    deviceType = battery?.myDevice?.deviceType ?: DeviceType.PHONE,
                    percent = percent,
                    isCharging = isCharging,
                    deviceName = battery?.myDevice?.name.toString(),
                    modifier = GlanceModifier.defaultWeight()
                )
                connectedDevice?.forEach { device ->
                    Spacer(modifier = GlanceModifier.height(PADDING))
                    BatteryItem(
                        deviceType = device.deviceType,
                        percent = device.batteryInPercentage,
                        isCharging = false,
                        deviceName = device.name.toString(),
                        modifier = GlanceModifier.defaultWeight()
                    )
                }
            }
        }
    }

    companion object {
        val BATTERY_PREFERENCES = stringPreferencesKey("batteryData")
        val WIDGET_PREFERENCES = stringPreferencesKey("widgetSize")
        private val PADDING = 8.dp
    }
}