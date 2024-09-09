package io.github.ifa.glancewidget.domain

import androidx.compose.runtime.Composable
import io.github.ifa.glancewidget.di.LocalRepositories
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import kotlinx.coroutines.flow.Flow

interface BatteryStateRepository {
    @Composable
    fun battery(): BatteryData
    fun batteryFlow(): Flow<BatteryData>
    fun extraBatteryFlow(): Flow<ExtraBatteryInfo>
    suspend fun saveExtraBatteryInformation()
    suspend fun saveWidgetTransparentSetting(isTransparent: Boolean, appWidgetId: Int)
}

@Composable
fun localBatteryRepository(): BatteryStateRepository {
    return LocalRepositories.current[BatteryStateRepository::class] as BatteryStateRepository
}
