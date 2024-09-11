package io.github.ifa.glancewidget.domain

import androidx.compose.runtime.Composable
import io.github.ifa.glancewidget.di.LocalRepositories
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun get(): Flow<AppSettings>
    suspend fun saveLocaleLanguage(language: AppSettings.Language)
    suspend fun saveTheme(themeType: ThemeType)
    suspend fun saveNotificationSetting(notificationSetting: AppSettings.NotificationSetting)
}

@Composable
fun localAppSettingsRepository(): AppSettingsRepository {
    return LocalRepositories.current[AppSettingsRepository::class] as AppSettingsRepository
}