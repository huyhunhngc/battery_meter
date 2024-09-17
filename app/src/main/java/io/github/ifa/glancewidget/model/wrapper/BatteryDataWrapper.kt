package io.github.ifa.glancewidget.model.wrapper

import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ChargeDisChargeCurrent
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.utils.toHHMMSS

data class BatteryDataWrapper(
    val batteryData: BatteryData = BatteryData.initial(),
    val extraBatteryInfo: ExtraBatteryInfo = ExtraBatteryInfo(),
    val powerDetails: PowerDetails = PowerDetails(0f, 0f),
    val chargeDisChargeCurrent: ChargeDisChargeCurrent = ChargeDisChargeCurrent()
) {
    val batteryHealth = batteryData.myDevice.getBatteryHealth(extraBatteryInfo)
    val remainBatteryTime =
        extraBatteryInfo.getBatteryTimeRemaining(
            batteryData.myDevice.isCharging,
            chargeDisChargeCurrent
        ).toHHMMSS()
    val remainChargeTime =
        extraBatteryInfo.getChargeTimeRemaining(
            batteryData.myDevice.isCharging,
            chargeDisChargeCurrent
        ).toHHMMSS()
}