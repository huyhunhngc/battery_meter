package io.github.ifa.glancewidget.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.BatteryManager
import android.os.Build
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.utils.Constants.MAX_DESIGN_CAPACITY
import io.github.ifa.glancewidget.utils.Constants.MIN_DESIGN_CAPACITY

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
        (chargeCounter / capacity * 100)
    }
    val chargingTimeRemaining = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        batteryManager.computeChargeTimeRemaining() / 1000
    } else {
        -1
    }
    return ExtraBatteryInfo(
        capacity = getDesignCapacity(),
        fullChargeCapacity = fullChargeCapacity,
        chargeCounter = chargeCounter,
        chargingTimeRemaining = chargingTimeRemaining
    )
}

fun Context.getDesignCapacity(): Int {
    val powerProfileClass = "com.android.internal.os.PowerProfile"
    val mPowerProfile = Class.forName(powerProfileClass).getConstructor(
        Context::class.java
    ).newInstance(this)
    val designCapacity = (Class.forName(powerProfileClass).getMethod(
        "getBatteryCapacity"
    ).invoke(mPowerProfile) as Double).toInt()

    return when {
        designCapacity == 0 || designCapacity < MIN_DESIGN_CAPACITY || designCapacity > MAX_DESIGN_CAPACITY -> MIN_DESIGN_CAPACITY
        else -> designCapacity
    }
}

fun Context.getChargingTimeRemaining(myDevice: MyDevice, extraBatteryInfo: ExtraBatteryInfo): String {
    var chargingTimeRemaining: Double
    val batteryLevel = myDevice.level
    val currentCapacity = extraBatteryInfo.chargeCounter
    val residualCapacity = extraBatteryInfo.capacity
    return "-"
}