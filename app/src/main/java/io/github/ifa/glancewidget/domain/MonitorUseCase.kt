package io.github.ifa.glancewidget.domain

import io.github.ifa.glancewidget.data.batteryWidgetStore
import io.github.ifa.glancewidget.glance.battery.BatteryWidget.Companion.BATTERY_PREFERENCES
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.BatteryMeterNotification
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.utils.setObject
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
        batteryStateRepository.setBatteryData(batteryData)
        batteryStateRepository.saveExtraBatteryInformation()
    }
    fun setChargingStatus(isCharging: Boolean) {
        batteryData = batteryData.setChargingStatus(isCharging)
    }

    fun setPairedDevices(pairedDevices: List<BonedDevice>) {
        batteryData = batteryData.copy(
            batteryConnectedDevices = pairedDevices.ifEmpty { batteryData.batteryConnectedDevices }
        )
    }

    fun updateBatteryDevice(myDevice: MyDevice) {
        batteryData = batteryData.copy(myDevice = myDevice)
    }

    suspend fun changePairedDevicesVisibility(showPairedDevices: Boolean) {

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