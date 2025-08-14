package io.github.ifa.glancewidget.data.battery

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.ifa.glancewidget.glance.battery.BatteryWidget.Companion.BATTERY_PREFERENCES
import io.github.ifa.glancewidget.glance.battery.BatteryWidget.Companion.WIDGET_PREFERENCES
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ChargeDisChargeCurrent
import io.github.ifa.glancewidget.model.ChartRecord
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.WidgetSettings
import io.github.ifa.glancewidget.utils.fromJson
import io.github.ifa.glancewidget.utils.getObject
import io.github.ifa.glancewidget.utils.setObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class BatteryDataStore(
    private val dataStore: DataStore<Preferences>,
) {
    fun get(): Flow<BatteryData> {
        return dataStore.data.map { preferences ->
            fromJson<BatteryData>(preferences[BATTERY_PREFERENCES])
        }.map {
            it ?: BatteryData.initial()
        }.flowOn(Dispatchers.IO)
    }

    fun getExtraBatteryInformation(default: ExtraBatteryInfo): Flow<ExtraBatteryInfo> {
        return dataStore.data.map { preferences ->
            fromJson<ExtraBatteryInfo>(preferences[EXTRA_BATTERY_PREFERENCES])
        }.map {
            it ?: default
        }.flowOn(Dispatchers.IO)
    }

    suspend fun saveExtraBatteryInformation(extraBatteryInfo: ExtraBatteryInfo) {
        dataStore.setObject(EXTRA_BATTERY_PREFERENCES, extraBatteryInfo)
    }

    suspend fun getWidgetSettings(): WidgetSettings {
        return dataStore.getObject<WidgetSettings>(WIDGET_PREFERENCES) ?: WidgetSettings()
    }

    suspend fun saveWidgetSettings(widgetSettings: WidgetSettings) {
        dataStore.setObject(WIDGET_PREFERENCES, widgetSettings)
    }

    fun getChartRecordFlow(): Flow<ChartRecord> {
        return dataStore.data.map { preferences ->
            fromJson<ChartRecord>(preferences[CHART_RECORD_PREFERENCES])
        }.map {
            it ?: ChartRecord()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun saveChartRecord(chartRecord: ChartRecord) {
        dataStore.setObject(CHART_RECORD_PREFERENCES, chartRecord)
    }

    suspend fun getChartRecord(): ChartRecord {
        return dataStore.getObject<ChartRecord>(CHART_RECORD_PREFERENCES) ?: ChartRecord()
    }

    fun getChargeCurrentFlow(): Flow<ChargeDisChargeCurrent> {
        return dataStore.data.map { preferences ->
            fromJson<ChargeDisChargeCurrent>(preferences[CHARGE_CURRENT_PREFERENCES])
        }.map {
            it ?: ChargeDisChargeCurrent()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getChargeCurrent(): ChargeDisChargeCurrent {
        return dataStore.getObject<ChargeDisChargeCurrent>(CHARGE_CURRENT_PREFERENCES)
            ?: ChargeDisChargeCurrent()
    }

    suspend fun saveChargeCurrent(chargeDisChargeCurrent: ChargeDisChargeCurrent) {
        dataStore.setObject(CHARGE_CURRENT_PREFERENCES, chargeDisChargeCurrent)
    }

    companion object {
        val EXTRA_BATTERY_PREFERENCES = stringPreferencesKey("extraBatteryData")
        val CHARGE_CURRENT_PREFERENCES = stringPreferencesKey("chargeCurrentData")
        val CHART_RECORD_PREFERENCES = stringPreferencesKey("chartRecordData")
    }
}