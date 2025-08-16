package io.github.ifa.glancewidget.data.appsettings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
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

    suspend fun saveBondedDeviceSetting(bondedDevice: BonnedDeviceSettings.BonnedDeviceSetting) {
        val currentSettings = getBondedDeviceSettings().settings
        val updatedSettings = currentSettings.toMutableMap().apply {
            put(bondedDevice.address, bondedDevice)
        }
        dataStore.setObject(BONDED_DEVICES_PREFERENCES, BonnedDeviceSettings(updatedSettings))
    }

    suspend fun getBondedDeviceSettings(): BonnedDeviceSettings {
        return dataStore.getObject<BonnedDeviceSettings>(BONDED_DEVICES_PREFERENCES)
            ?: BonnedDeviceSettings()
    }

    fun getBondedDevicesFlow(): Flow<BonnedDeviceSettings> {
        return dataStore.data.map { preferences ->
            fromJson<BonnedDeviceSettings>(preferences[BONDED_DEVICES_PREFERENCES])
        }.map {
            it ?: BonnedDeviceSettings()
        }
    }
    companion object {
        val SETTINGS_PREFERENCES = stringPreferencesKey("settings")
        val BONDED_DEVICES_PREFERENCES = stringPreferencesKey("bonned_devices")
    }
}