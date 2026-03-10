package io.github.ifa.glancewidget.domain

import androidx.compose.runtime.Composable
import io.github.ifa.glancewidget.di.LocalRepositories
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ChargeDisChargeCurrent
import io.github.ifa.glancewidget.model.ChartRecord
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.model.WidgetSetting
import kotlinx.coroutines.flow.Flow

interface BatteryStateRepository {
    @Composable
    fun battery(): BatteryData
    fun batteryFlow(): Flow<BatteryData>
    suspend fun batteryData(): BatteryData
    fun extraBatteryFlow(): Flow<ExtraBatteryInfo>
    suspend fun extraBattery(): ExtraBatteryInfo
    fun chargeCurrentFlow(): Flow<ChargeDisChargeCurrent>
    fun chartRecordFlow(): Flow<ChartRecord>
    suspend fun chargeCurrent(): ChargeDisChargeCurrent
    suspend fun saveExtraBatteryInformation()
    suspend fun setBatteryData(batteryData: BatteryData)
    suspend fun changePairedDevicesVisibility(showPairedDevices: Boolean)
    suspend fun saveChargeCurrent(chargeCurrent: Int)
    suspend fun setMyDevice(myDevice: MyDevice)
    suspend fun saveWidgetInitialSetting(
        appWidgetId: Int,
        isTransparent: Boolean,
        widgetStyle: WidgetSetting.Style
    )
    suspend fun saveHistoryForChart(temperature: Float, voltage: Float)
}

@Composable
fun localBatteryRepository(): BatteryStateRepository {
    return LocalRepositories.current[BatteryStateRepository::class] as BatteryStateRepository
}
