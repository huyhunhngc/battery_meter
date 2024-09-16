package io.github.ifa.glancewidget.domain

import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.model.wrapper.PowerDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class BatteryUseCase @Inject constructor(
    private val batteryStateRepository: BatteryStateRepository,
    private val appSettingsRepository: AppSettingsRepository,
) {
    fun getBatteryWrapper(): Flow<BatteryDataWrapper> {
        return combine(
            batteryStateRepository.batteryFlow(),
            batteryStateRepository.extraBatteryFlow(),
            appSettingsRepository.get()
        ) { batteryData, extraBatteryInfo, appSettings ->
            BatteryDataWrapper(
                batteryData = batteryData.applySetting(appSettings),
                extraBatteryInfo = extraBatteryInfo,
                powerDetails = extractPower(batteryData, extraBatteryInfo)
            )
        }
    }

    fun getPower(): Flow<PowerDetails> {
        return combine(
            batteryStateRepository.batteryFlow(),
            batteryStateRepository.extraBatteryFlow()
        ) { batteryData, extraBatteryInfo ->
            extractPower(batteryData, extraBatteryInfo)
        }
    }

    private fun BatteryData.applySetting(appSettings: AppSettings): BatteryData {
        return copy(
            batteryConnectedDevices = if (appSettings.notificationSetting.showPairedDevices) {
                batteryConnectedDevices
            } else {
                emptyList()
            }
        )
    }

    private fun extractPower(
        batteryData: BatteryData,
        extraBatteryInfo: ExtraBatteryInfo
    ): PowerDetails {
        val power = extraBatteryInfo.powerInWatt(
            batteryData.myDevice.voltage,
            batteryData.myDevice.isCharging
        ).toFloat()
        val powerPercentage = power / extraBatteryInfo.maxWattsChargeInput
        return PowerDetails(
            power = power,
            powerPercentage = powerPercentage
        )
    }
}