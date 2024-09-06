package io.github.ifa.glancewidget.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.BatteryManager
import io.github.ifa.glancewidget.model.ExtraBatteryInfo

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

fun Context.getExtraBatteryInformation(): ExtraBatteryInfo {
    val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val chargeCounter =
        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    val fullChargeCapacity = if (chargeCounter == Int.MIN_VALUE || capacity == Int.MIN_VALUE) {
        0
    } else {
        (chargeCounter / capacity * 100).toLong()
    }
    return ExtraBatteryInfo(
        capacity = capacity.toLong(),
        fullChargeCapacity = fullChargeCapacity,
        chargeCounter = chargeCounter.toLong()
    )
}