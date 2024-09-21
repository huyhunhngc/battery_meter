package io.github.ifa.glancewidget.domain

import androidx.compose.runtime.Composable
import io.github.ifa.glancewidget.di.LocalRepositories
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import io.github.ifa.glancewidget.model.ThemeTypeColor
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun get(): Flow<AppSettings>
    suspend fun getAppSettings():AppSettings
    suspend fun saveLocaleLanguage(language: AppSettings.Language)
    suspend fun saveTheme(themeType: ThemeType)
    suspend fun saveThemeColor(themeTypeColor: ThemeTypeColor)
    suspend fun saveNotificationSetting(notificationSetting: AppSettings.NotificationSetting)
    suspend fun saveShowPairedDevicesSetting(showPairedDevices: Boolean)
}

@Composable
fun localAppSettingsRepository(): AppSettingsRepository {
    return LocalRepositories.current[AppSettingsRepository::class] as AppSettingsRepository
}