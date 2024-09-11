package io.github.ifa.glancewidget.data.appsettings

import android.content.Context
import io.github.ifa.glancewidget.domain.AppSettingsRepository
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.utils.setLocale
import kotlinx.coroutines.flow.Flow

class DefaultAppSettingsRepository(
    private val appSettingDataStore: AppSettingDataStore,
    private val context: Context
): AppSettingsRepository {
    override fun get(): Flow<AppSettings> {
        return appSettingDataStore.getSettingsFlow()
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

    override suspend fun saveNotificationSetting(notificationSetting: AppSettings.NotificationSetting) {
        val settings = appSettingDataStore.getSettings()
        appSettingDataStore.saveSettings(settings.copy(notificationSetting = notificationSetting))
    }
}