package io.github.ifa.glancewidget.data.battery

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.WidgetSetting
import io.github.ifa.glancewidget.utils.chunked
import io.github.ifa.glancewidget.utils.getExtraBatteryInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map

class DefaultBatteryStateRepository(
    private val batteryDataStore: BatteryDataStore, private val context: Context
) : BatteryStateRepository {
    @Composable
    override fun battery(): BatteryData {
        val result by remember {
            batteryDataStore.get()
        }.collectAsState(BatteryData.initial())
        return result
    }

    override fun batteryFlow(): Flow<BatteryData> {
        return batteryDataStore.get()
    }

    override fun extraBatteryFlow(): Flow<ExtraBatteryInfo> {
        return batteryDataStore.getExtraBatteryInformation()
            .chunked(4)
            .conflate()
            .map { it.average() }
    }

    override suspend fun saveExtraBatteryInformation() {
        val extraBatteryInfo = context.getExtraBatteryInformation()
        batteryDataStore.saveExtraBatteryInformation(extraBatteryInfo)
    }

    override suspend fun saveWidgetTransparentSetting(isTransparent: Boolean, appWidgetId: Int) {
        val widgetSettings = batteryDataStore.getWidgetSettings()
        val savedSetting = widgetSettings.settings[appWidgetId] ?: WidgetSetting()
        val newWidgetSetting = savedSetting.copy(
            isTransparent = isTransparent
        )
        val newSettings =
            widgetSettings.copy(settings = widgetSettings.settings.toMutableMap().apply {
                this[appWidgetId] = newWidgetSetting
            })
        batteryDataStore.saveWidgetSettings(newSettings)
    }
}

fun List<ExtraBatteryInfo>.average(): ExtraBatteryInfo {
    return ExtraBatteryInfo(
        capacity = this.map { it.capacity }.average().toInt(),
        chargeCounter = this.map { it.chargeCounter }.average().toInt(),
        fullChargeCapacity = this.map { it.fullChargeCapacity }.average().toInt(),
        chargingTimeRemaining = this.map { it.chargingTimeRemaining }.average().toLong(),
        chargeCurrent = this.map { it.chargeCurrent }.average().toInt(),
    )
}