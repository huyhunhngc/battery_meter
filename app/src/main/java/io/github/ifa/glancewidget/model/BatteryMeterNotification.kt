package io.github.ifa.glancewidget.model

data class BatteryMeterNotification(
    val batteryLevel: Int,
    val isCharging: Boolean,
    val temperature: MyDevice.Temperature? = null,
    val remainBatteryTime: String = "--",
    val remainChargeTime: String = "--"
)
