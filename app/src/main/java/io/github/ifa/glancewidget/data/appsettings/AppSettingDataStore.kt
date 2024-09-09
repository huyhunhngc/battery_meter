package io.github.ifa.glancewidget.data.appsettings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.utils.fromJson
import io.github.ifa.glancewidget.utils.getObject
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingDataStore(private val dataStore: DataStore<Preferences>) {
    fun getSettingsFlow(): Flow<AppSettings> {
        return dataStore.data.map { preferences ->
            fromJson<AppSettings>(preferences[SETTINGS_PREFERENCES])
        }.map {
            it ?: AppSettings()
        }
    }
    suspend fun getSettings(): AppSettings {
        return dataStore.getObject<AppSettings>(SETTINGS_PREFERENCES) ?: AppSettings()
    }
    suspend fun saveSettings(settings: AppSettings) {
        dataStore.setObject(SETTINGS_PREFERENCES, settings)
    }
    companion object {
        val SETTINGS_PREFERENCES = stringPreferencesKey("settings")
    }
}