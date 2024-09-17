package io.github.ifa.glancewidget.data.battery

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ChargeDisChargeCurrent
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.WidgetSetting
import io.github.ifa.glancewidget.utils.Constants.DEFAULT_MAX_COLLECT_CURRENT
import io.github.ifa.glancewidget.utils.chunked
import io.github.ifa.glancewidget.utils.getExtraBatteryInformation
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
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

    override fun extraBattery(): ExtraBatteryInfo {
        return context.getExtraBatteryInformation()
    }

    override fun extraBatteryFlow(): Flow<ExtraBatteryInfo> {
        return flow {
            emit(extraBattery())
            batteryDataStore.getExtraBatteryInformation()
                .chunked(3)
                .conflate()
                .map { it.average() }
                .collect { chunk -> emit(chunk) }
        }
    }

    override fun chargeCurrentFlow(): Flow<ChargeDisChargeCurrent> {
        return batteryDataStore.getChargeCurrentFlow()
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


    override suspend fun saveChargeCurrent(chargeCurrent: Int) {
        val chargeDisChargeCurrent = batteryDataStore.getChargeCurrent()
        val chargeSpeed = ChargeDisChargeCurrent.getChargeSpeed(chargeCurrent)
        val newChargeDisChargeCurrent = when (chargeSpeed) {
            ChargeDisChargeCurrent.ChargeSpeed.FAST -> {
                chargeDisChargeCurrent.copy(
                    fastChargeCurrents = chargeDisChargeCurrent.fastChargeCurrents.addUpTo(
                        DEFAULT_MAX_COLLECT_CURRENT,
                        chargeCurrent
                    )
                )
            }

            ChargeDisChargeCurrent.ChargeSpeed.TURBO -> {
                chargeDisChargeCurrent.copy(
                    turboChargeCurrents = chargeDisChargeCurrent.turboChargeCurrents.addUpTo(
                        DEFAULT_MAX_COLLECT_CURRENT,
                        chargeCurrent
                    )
                )
            }

            ChargeDisChargeCurrent.ChargeSpeed.NORMAL -> {
                chargeDisChargeCurrent.copy(
                    chargeCurrents = chargeDisChargeCurrent.chargeCurrents.addUpTo(
                        DEFAULT_MAX_COLLECT_CURRENT,
                        chargeCurrent
                    )
                )
            }

            ChargeDisChargeCurrent.ChargeSpeed.DISCHARGING -> {
                chargeDisChargeCurrent.copy(
                    dischargeCurrents = chargeDisChargeCurrent.dischargeCurrents.addUpTo(
                        DEFAULT_MAX_COLLECT_CURRENT,
                        chargeCurrent
                    )
                )
            }
        }
        Log.d("!@#", "saveChargeCurrent: $newChargeDisChargeCurrent")
        batteryDataStore.saveChargeCurrent(newChargeDisChargeCurrent)
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

fun List<Int>.addUpTo(limit: Int, value: Int): List<Int> {
    return this.toPersistentList().add(value).takeLast(limit)
}