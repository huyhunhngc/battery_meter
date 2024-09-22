package io.github.ifa.glancewidget.utils

import android.annotation.SuppressLint
import android.app.LocaleManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.res.Configuration
import android.os.BatteryManager
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.DeviceType
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.utils.Constants.MAX_DESIGN_CAPACITY
import io.github.ifa.glancewidget.utils.Constants.MIN_DESIGN_CAPACITY
import java.util.Locale

@SuppressLint("MissingPermission")
fun Context.getPairedDevices(): List<BonedDevice> {
    val pairedDevices = kotlin.runCatching {
        val btManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        btManager.adapter.bondedDevices
    }.getOrDefault(emptyList())

    return pairedDevices.map {
        BonedDevice(
            name = it.name,
            address = it.address,
            batteryInPercentage = it.batteryLevel,
            batteryInMinutes = 0,
            deviceType = DeviceType.fromClass(it.bluetoothClass.deviceClass)
        )
    }.sortedBy { it.deviceType.ordinal }
}

fun Context.getExtraBatteryInformation(): ExtraBatteryInfo {
    val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val propertyChargeCounter =
        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    // this value is current charge counter in micro amp hours. But some device return in millis
    val chargeCounter = if (propertyChargeCounter / 1000 < MAX_DESIGN_CAPACITY / 1000) {
        propertyChargeCounter
    } else {
        propertyChargeCounter / 1000
    }
    val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    val fullChargeCapacity = chargeCounter.toFloat() / capacity.toFloat() * 100f
    val chargingTimeRemaining = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        batteryManager.computeChargeTimeRemaining() / 1000
    } else {
        -1
    }

    val chargeCurrent = batteryManager.getIntProperty(
        BatteryManager.BATTERY_PROPERTY_CURRENT_NOW
    ) / 1_000 // micro to millis

    return ExtraBatteryInfo(
        capacity = getDesignCapacity(),
        fullChargeCapacity = fullChargeCapacity.roundToNearestHundred(),
        chargeCounter = chargeCounter,
        chargingTimeRemaining = chargingTimeRemaining,
        chargeCurrent = chargeCurrent
    )
}

fun Float.roundToNearestHundred(): Int {
    return (this.toInt() + 50) / 100 * 100
}

@SuppressLint("PrivateApi")
private fun Context.getDesignCapacity(): Int {
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

fun Context.setLocale(localeCode: String) {
    val code = if (localeCode == AppSettings.Language.DEFAULT.code) {
        ""
    } else {
        localeCode
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        setLocaleForDevicesHigherApi33(code)
    } else {
        setLocaleForDevicesLowerApi33(code)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun Context.setLocaleForDevicesHigherApi33(localeCode: String) {
    getSystemService(LocaleManager::class.java).applicationLocales =
        LocaleList.forLanguageTags(localeCode)
}

private fun setLocaleForDevicesLowerApi33(localeTag: String) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(localeTag))
}