package io.github.ifa.glancewidget.domain

import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.BatteryMeterNotification
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.MyDevice
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class MonitorUseCase @Inject constructor(
    private val batteryStateRepository: BatteryStateRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val batteryUseCase: BatteryUseCase,
) {
    private val lock = Object()
    var batteryData: BatteryData = BatteryData.initial()
        private set(value) {
            synchronized(lock) {
                field = value
            }
        }

    suspend fun refreshBatteryInformation() {
        batteryStateRepository.saveExtraBatteryInformation()
    }

    suspend fun setChargingStatus(isCharging: Boolean) {
        batteryData = batteryData.setChargingStatus(isCharging)
        batteryStateRepository.setBatteryData(batteryData)
    }

    suspend fun setPairedDevices(pairedDevices: List<BonedDevice>) {
        batteryData = batteryData.copy(
            batteryConnectedDevices = pairedDevices.ifEmpty { batteryData.batteryConnectedDevices })
        batteryStateRepository.setBatteryData(batteryData)
    }

    suspend fun updateBatteryDevice(myDevice: MyDevice) {
        batteryData = batteryData.copy(myDevice = myDevice)
        batteryStateRepository.setBatteryData(batteryData)
    }

    suspend fun changePairedDevicesVisibility(showPairedDevices: Boolean) {
        batteryStateRepository.changePairedDevicesVisibility(showPairedDevices)
    }

    suspend fun onDetectBatteryInfo(onDetect: (BatteryMeterNotification) -> Unit) {
        if (!appSettingsRepository.getAppSettings().notificationSetting.batteryAlert) return
        val batteryWrapper = batteryUseCase.getBatteryWrapper().firstOrNull()
        val notification = if (batteryWrapper != null) {
            BatteryMeterNotification(
                batteryLevel = batteryWrapper.batteryData.myDevice.level,
                isCharging = batteryWrapper.batteryData.myDevice.isCharging,
                remainBatteryTime = batteryWrapper.remainBatteryTime,
                remainChargeTime = batteryWrapper.remainChargeTime,
                temperature = batteryWrapper.batteryData.myDevice.temperature
            )
        } else {
            BatteryMeterNotification(
                batteryLevel = batteryData.myDevice.level,
                isCharging = batteryData.myDevice.isCharging,
                temperature = batteryData.myDevice.temperature
            )
        }
        onDetect(notification)
    }
}