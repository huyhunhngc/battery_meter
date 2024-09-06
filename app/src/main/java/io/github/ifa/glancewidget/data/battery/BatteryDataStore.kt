package io.github.ifa.glancewidget.data.battery

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.ifa.glancewidget.glance.battery.BatteryWidget.Companion.BATTERY_PREFERENCES
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.utils.fromJson
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BatteryDataStore(
    private val dataStore: DataStore<Preferences>,
) {
    fun get(): Flow<BatteryData> {
        return dataStore.data.map { preferences ->
            fromJson<BatteryData>(preferences[BATTERY_PREFERENCES])
        }.map {
            it ?: BatteryData.initial()
        }
    }

    fun getExtraBatteryInformation(): Flow<ExtraBatteryInfo> {
        return dataStore.data.map { preferences ->
            fromJson<ExtraBatteryInfo>(preferences[EXTRA_BATTERY_PREFERENCES])
        }.map {
            it ?: ExtraBatteryInfo()
        }
    }

    suspend fun saveExtraBatteryInformation(extraBatteryInfo: ExtraBatteryInfo) {
        dataStore.setObject(EXTRA_BATTERY_PREFERENCES, extraBatteryInfo)
    }

    companion object {
        val EXTRA_BATTERY_PREFERENCES = stringPreferencesKey("extraBatteryData")
    }
}