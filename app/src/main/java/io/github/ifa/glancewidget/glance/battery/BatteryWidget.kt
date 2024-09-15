package io.github.ifa.glancewidget.glance.battery

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.unit.ColorProvider
import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.glance.battery.component.BatteryItem
import io.github.ifa.glancewidget.glance.battery.component.FullWidthItem
import io.github.ifa.glancewidget.glance.battery.component.GridWrapItem
import io.github.ifa.glancewidget.glance.helper.getSettingByGlance
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.model.WidgetSetting
import io.github.ifa.glancewidget.model.WidgetSettings
import io.github.ifa.glancewidget.utils.fromJson
import io.github.ifa.glancewidget.utils.getObject
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.flow.first

class BatteryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batteryWidgetStore = context.batteryWidgetStore
        val initial = batteryWidgetStore.data.first()

        provideContent {
            val data by batteryWidgetStore.data.collectAsState(initial)
            val widgetSettingsJson by rememberUpdatedState(data[WIDGET_PREFERENCES])
            val batteryJson by rememberUpdatedState(data[BATTERY_PREFERENCES])
            val showPairedDevices by rememberUpdatedState(data[SHOW_PAIRED_DEVICES] ?: true)
            val battery = remember(batteryJson) {
                fromJson<BatteryData>(batteryJson)
            }
            val setting = remember(widgetSettingsJson) {
                val settings = fromJson<WidgetSettings>(widgetSettingsJson)
                settings?.getSettingByGlance(context, id)
            }
            GlanceTheme {
                Content(battery = battery, setting = setting, showPairedDevices = showPairedDevices)
            }
        }
    }

    suspend fun updateIfBatteryChanged(
        context: Context, glanceId: GlanceId
    ) {
        update(context, glanceId)
    }

    suspend fun updateOnSizeChanged(
        context: Context, glanceId: GlanceId, widgetSetting: WidgetSetting
    ) {
        val store = context.batteryWidgetStore
        val widgetSettings = store.getObject<WidgetSettings>(WIDGET_PREFERENCES) ?: WidgetSettings()
        val savedSetting = widgetSettings.getSettingByGlance(context, glanceId) ?: WidgetSetting()
        val newWidgetSetting = savedSetting.copy(
            appWidgetId = widgetSetting.appWidgetId,
            width = widgetSetting.width,
            height = widgetSetting.height
        )
        val newSettings =
            widgetSettings.copy(settings = widgetSettings.settings.toMutableMap().apply {
                this[newWidgetSetting.appWidgetId] = newWidgetSetting
            })
        store.setObject(WIDGET_PREFERENCES, newSettings)
        update(context, glanceId)
    }

    @Composable
    private fun Content(
        battery: BatteryData?, setting: WidgetSetting?, showPairedDevices: Boolean = true
    ) {
        val percent = battery?.myDevice?.level ?: 100
        val isCharging = battery?.myDevice?.isCharging ?: false

        val typeWidget = remember(setting) {
            setting?.getType() ?: WidgetSetting.Type.Small
        }

        val connectedDevice = remember(typeWidget, battery, showPairedDevices) {
            battery?.batteryConnectedDevices?.take(
                if (showPairedDevices) typeWidget.itemOnSize() else 0
            )
        }

        val isTransparent = remember(setting) {
            setting?.isTransparent ?: false
        }

        Box(
            modifier = GlanceModifier.fillMaxSize().padding(PADDING).background(
                if (isTransparent) {
                    ColorProvider(Color.Transparent)
                } else {
                    GlanceTheme.colors.widgetBackground
                }
            ),
        ) {
            Column(modifier = GlanceModifier.fillMaxSize()) {
                BatteryItem(
                    deviceType = battery?.myDevice?.deviceType ?: DeviceType.PHONE,
                    percent = percent,
                    isCharging = isCharging,
                    deviceName = battery?.myDevice?.name.toString(),
                    modifier = GlanceModifier.defaultWeight()
                )
                if (!connectedDevice.isNullOrEmpty()) {
                    when (typeWidget) {
                        WidgetSetting.Type.FullWidex1, WidgetSetting.Type.Wide -> {
                            GridWrapItem(
                                connectedDevice = connectedDevice,
                                modifier = GlanceModifier.defaultWeight().fillMaxWidth()
                            )
                        }

                        else -> {
                            FullWidthItem(
                                connectedDevices = connectedDevice,
                                modifier = GlanceModifier.defaultWeight().fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        val BATTERY_PREFERENCES = stringPreferencesKey("batteryData")
        val WIDGET_PREFERENCES = stringPreferencesKey("widgetSetting")
        val SHOW_PAIRED_DEVICES = booleanPreferencesKey("showPairedDevices")
        val PADDING = 8.dp
    }
}