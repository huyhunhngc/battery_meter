package io.github.ifa.glancewidget.domain

import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.ThemeType
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun get(): Flow<AppSettings>
    suspend fun saveLocaleLanguage(language: AppSettings.Language)
    suspend fun saveTheme(themeType: ThemeType)
}