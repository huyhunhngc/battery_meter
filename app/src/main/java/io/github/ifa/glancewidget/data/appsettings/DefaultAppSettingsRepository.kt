package io.github.ifa.glancewidget.data.appsettings

import android.content.Context
import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.glance.battery.BatteryWidget
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import io.github.ifa.glancewidget.utils.setLocale
import kotlinx.coroutines.flow.Flow

class DefaultAppSettingsRepository(
    private val appSettingDataStore: AppSettingDataStore,
    private val context: Context
) : AppSettingsRepository {
    override fun get(): Flow<AppSettings> {
        return appSettingDataStore.getSettingsFlow()
    }

    override suspend fun getAppSettings(): AppSettings {
        return appSettingDataStore.getSettings()
    }

    override suspend fun saveLocaleLanguage(language: AppSettings.Language) {
        val settings = appSettingDataStore.getSettings()
        appSettingDataStore.saveSettings(settings.copy(language = language))
        context.setLocale(language.code)
    }

    override suspend fun saveTheme(themeType: ThemeType) {
        val settings = appSettingDataStore.getSettings()
        appSettingDataStore.saveSettings(settings.copy(theme = themeType))
    }

    override suspend fun saveThemeColor(themeTypeColor: ThemeTypeColor) {
        val settings = appSettingDataStore.getSettings()
        appSettingDataStore.saveSettings(settings.copy(themeColor = themeTypeColor))
    }

    override suspend fun saveShowPairedDevicesSetting(showPairedDevices: Boolean) {
        val settings = appSettingDataStore.getSettings()
        appSettingDataStore.saveSettings(
            settings.copy(
                notificationSetting = settings.notificationSetting.copy(
                    showPairedDevices = showPairedDevices
                )
            )
        )
    }

    override suspend fun saveBondedDeviceSetting(macAddress: String, showInWidget: Boolean) {
        appSettingDataStore.saveBondedDeviceSetting(
            BonnedDeviceSettings.BonnedDeviceSetting(
                address = macAddress,
                showInWidget = showInWidget
            )
        )
        try {
            context.batteryWidgetStore.updateData { preferences ->
                val currentHiddenDevices =
                    preferences[BatteryWidget.DEVICE_HIDDEN_BY_ADDRESS] ?: emptySet()
                if (showInWidget) {
                    preferences.toMutablePreferences().apply {
                        this[BatteryWidget.DEVICE_HIDDEN_BY_ADDRESS] =
                            currentHiddenDevices - macAddress
                    }
                } else {
                    preferences.toMutablePreferences().apply {
                        this[BatteryWidget.DEVICE_HIDDEN_BY_ADDRESS] =
                            (currentHiddenDevices + macAddress).distinct().toSet()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getBondedDevices(): Flow<BonnedDeviceSettings> {
        return appSettingDataStore.getBondedDevicesFlow()
    }

    override suspend fun saveNotificationSetting(notificationSetting: AppSettings.NotificationSetting) {
        val settings = appSettingDataStore.getSettings()
        appSettingDataStore.saveSettings(settings.copy(notificationSetting = notificationSetting))
    }
}