package io.github.ifa.glancewidget.glance.battery

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.glance.battery.component.CircleBatteryWidget
import io.github.ifa.glancewidget.glance.battery.component.HorizontalBatteryWidget
import io.github.ifa.glancewidget.glance.battery.ui.PixelBatteryTheme
import io.github.ifa.glancewidget.glance.helper.getSettingByGlance
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.WidgetSetting
import io.github.ifa.glancewidget.model.WidgetSettings
import io.github.ifa.glancewidget.utils.fromJson
import io.github.ifa.glancewidget.utils.getObject
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.flow.firstOrNull

class BatteryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batteryWidgetStore = context.batteryWidgetStore
        val initial = batteryWidgetStore.data.firstOrNull()

        provideContent {
            val data by batteryWidgetStore.data.collectAsState(initial)
            val widgetSettingsJson by rememberUpdatedState(data?.get(WIDGET_PREFERENCES))
            val batteryJson by rememberUpdatedState(data?.get(BATTERY_PREFERENCES))
            val showPairedDevices by rememberUpdatedState(data?.get(SHOW_PAIRED_DEVICES) != false)
            val hiddenDevices by rememberUpdatedState(
                data?.get(DEVICE_HIDDEN_BY_ADDRESS) ?: emptySet()
            )
            val battery = remember(batteryJson) {
                fromJson<BatteryData>(batteryJson)
            }
            val settings = remember(widgetSettingsJson) {
                fromJson<WidgetSettings>(widgetSettingsJson) ?: WidgetSettings()
            }
            val setting = remember(widgetSettingsJson) {
                settings.getSettingByGlance(context, id)
            }
            PixelBatteryTheme(
                themeTypeColor = settings.themeColor,
                themeType = settings.theme
            ) {
                Content(
                    battery = battery,
                    setting = setting,
                    hiddenDevices = hiddenDevices,
                    showPairedDevices = showPairedDevices
                )
            }
        }
    }

    suspend fun updateIfBatteryChanged(
        context: Context, glanceId: GlanceId
    ) {
        update(context, glanceId)
    }

    suspend fun updateOnSizeChanged(
        context: Context,
        glanceId: GlanceId,
        widgetSetting: WidgetSetting
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

    suspend fun updateWidgetSetting(
        context: Context,
        transformNewSettings: WidgetSettings.() -> WidgetSettings
    ) {
        val store = context.batteryWidgetStore
        val savedSettings = store.getObject<WidgetSettings>(WIDGET_PREFERENCES) ?: WidgetSettings()
        val newWidgetSettings = savedSettings.transformNewSettings()
        store.setObject(WIDGET_PREFERENCES, newWidgetSettings)
        updateAll(context)
    }

    @Composable
    private fun Content(
        battery: BatteryData?,
        setting: WidgetSetting?,
        hiddenDevices: Set<String> = emptySet(),
        showPairedDevices: Boolean = false
    ) {
        val percent = battery?.myDevice?.level ?: 100
        val isCharging = battery?.myDevice?.isCharging == true

        val sizeWidget = remember(setting) {
            setting?.getType() ?: WidgetSetting.Type.Small
        }

        val connectedDevices = remember(battery, showPairedDevices) {
            if (showPairedDevices) {
                battery?.batteryConnectedDevices?.distinctBy { it.address }
                    ?.filter { it.address !in hiddenDevices }.orEmpty()
            } else {
                emptyList()
            }
        }

        val isTransparent = remember(setting) {
            setting?.isTransparent == true
        }

        val widgetStyle = remember(setting) {
            setting?.style ?: WidgetSetting.Style.Horizontal
        }

        when (widgetStyle) {
            WidgetSetting.Style.Horizontal -> HorizontalBatteryWidget(
                battery = battery,
                percent = percent,
                isCharging = isCharging,
                isTransparent = isTransparent,
                connectedDevice = connectedDevices,
                sizeWidget = sizeWidget
            )

            WidgetSetting.Style.Circle -> CircleBatteryWidget(
                battery = battery,
                percent = percent,
                isCharging = isCharging,
                isTransparent = isTransparent,
                connectedDevice = connectedDevices,
                sizeWidget = sizeWidget
            )

            else -> {}
        }
    }

    companion object {
        val BATTERY_PREFERENCES = stringPreferencesKey("batteryData")
        val WIDGET_PREFERENCES = stringPreferencesKey("widgetSetting")
        val SHOW_PAIRED_DEVICES = booleanPreferencesKey("showPairedDevices")
        val DEVICE_HIDDEN_BY_ADDRESS = stringSetPreferencesKey("deviceHiddenByAddress")
        val PADDING = 8.dp
    }
}