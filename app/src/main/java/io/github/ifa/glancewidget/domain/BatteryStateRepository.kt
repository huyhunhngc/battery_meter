package io.github.ifa.glancewidget.domain

import androidx.compose.runtime.Composable
import io.github.ifa.glancewidget.di.LocalRepositories
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ChargeDisChargeCurrent
import io.github.ifa.glancewidget.model.ChartRecord
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import kotlinx.coroutines.flow.Flow

interface BatteryStateRepository {
    @Composable
    fun battery(): BatteryData
    fun batteryFlow(): Flow<BatteryData>
    fun extraBattery(): ExtraBatteryInfo
    fun extraBatteryFlow(): Flow<ExtraBatteryInfo>
    fun chargeCurrentFlow(): Flow<ChargeDisChargeCurrent>
    fun chartRecordFlow(): Flow<ChartRecord>
    suspend fun chargeCurrent(): ChargeDisChargeCurrent
    suspend fun saveExtraBatteryInformation()
    suspend fun saveChargeCurrent(chargeCurrent: Int)
    suspend fun saveWidgetTransparentSetting(isTransparent: Boolean, appWidgetId: Int)
    suspend fun saveHistoryForChart(temperature: Float, voltage: Float)
}

@Composable
fun localBatteryRepository(): BatteryStateRepository {
    return LocalRepositories.current[BatteryStateRepository::class] as BatteryStateRepository
}
