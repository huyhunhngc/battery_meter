package io.github.ifa.glancewidget.model.wrapper

import android.content.Context
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ChargeDisChargeCurrent
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.utils.toLocaleDuration

data class BatteryDataWrapper(
    val batteryData: BatteryData = BatteryData.initial(),
    val extraBatteryInfo: ExtraBatteryInfo = ExtraBatteryInfo(),
    val powerDetails: PowerDetails = PowerDetails(0f, 0f),
    val chargeDisChargeCurrent: ChargeDisChargeCurrent = ChargeDisChargeCurrent()
) {
    val batteryHealth = batteryData.myDevice.getBatteryHealth(extraBatteryInfo)
    fun remainBatteryTime(context: Context) = extraBatteryInfo.getBatteryTimeRemaining(
        batteryData.myDevice.isCharging && batteryData.myDevice.level < 100, chargeDisChargeCurrent
    ).toLocaleDuration(context)

    fun remainChargeTime(context: Context) = extraBatteryInfo.getChargeTimeRemaining(
        batteryData.myDevice.isCharging, chargeDisChargeCurrent
    ).toLocaleDuration(context)
}
